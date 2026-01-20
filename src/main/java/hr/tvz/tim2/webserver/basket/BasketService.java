package hr.tvz.tim2.webserver.basket;

import hr.tvz.tim2.webserver.dto.BasketDto;
import hr.tvz.tim2.webserver.security.repository.UserRepository;
import hr.tvz.tim2.webserver.stock.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import static hr.tvz.tim2.webserver.dto.DtoMapper.toDto;

@Slf4j
@Service
public class BasketService {
    private final StockService stockService;
    private final BasketDbRepository repo;
    private final BasketItemDbRepository itemRepo;
    private final UserRepository userRepo;

    private static final Duration ItemValidity = Duration.of(15, ChronoUnit.MINUTES);
    private static final Duration BasketValidity = Duration.of(3, ChronoUnit.HOURS);

    public BasketService(@Autowired StockService stockService,
                         @Autowired BasketDbRepository repo,
                         @Autowired BasketItemDbRepository itemRepo,
                         @Autowired UserRepository userRepo) {
        this.stockService = stockService;
        this.repo = repo;
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
        log.debug("BasketService created");
    }

    public BasketEntity getOrCreateActiveBasket(String userName) {
        log.debug("Getting or creating a basket for user: {}", userName);
        Long userId = getUserId(userName);
        var basket = repo.findFirstByUserIdAndValidUntilDateAfterAndStatusNot(userId, Instant.now(), BasketStatus.ORDERED);

        return basket.orElseGet(() -> {
            log.debug("Creating new basket for user: {}", userName);
            var created = new BasketEntity();
            userRepo.findById(userId).ifPresentOrElse(created::setUser,
                                                      () -> {throw new IllegalArgumentException("User doesn't exist!");});
            created.setStatus(BasketStatus.ACTIVE);
            created.setValidUntilDate(Instant.now().plusSeconds(60 * 60 *3));
            return repo.save(created);
        });
    }

    public BasketEntity refreshBasket(BasketEntity basket) {
        log.debug("Refreshing basket: {}", basket.getBasketId());
        basket.getBasketItems().forEach(item -> {
            try {
                addItemToBasket(basket, item.getItemId());
            }
            catch (Exception e) {
                log.error("Error while refreshing basket: {}", e.getMessage());
                throw new IllegalStateException("Error while refreshing basket.", e);
            }
        });
        return basket;
    }

    public BasketDto getBasketDto(String userName) {
        log.debug("Getting basket for user: {}", userName);
        BasketEntity entity = getOrCreateActiveBasket(userName);
        return toDto(entity);
    }

    public void addItemToBasket(String userName, String movieId) throws Exception {
        log.debug("Adding item to basket for user: {}", userName);
        BasketEntity basket = getOrCreateActiveBasket(userName);
        addItemToBasket(basket, movieId);
    }

    public void addItemToBasket(BasketEntity basket, String movieId) throws Exception {
        log.debug("Adding item to basket: {}", basket.getBasketId());
        // If the movie is not already in the basket.
        if (basket.getBasketItems().stream().noneMatch(i -> i.getItemId().equals(movieId))) {
            log.info("Adding item that doesn't exist.");
            try {
                stockService.reserveMovie(movieId);
            }
            catch (Exception e) {
                String message = String.format("Movie with id=%s could not be reserved: %s", movieId, e.getMessage());
                log.error(message);
                throw new IllegalStateException(message, e);
            }
            BasketItemEntity item = createAndPersistBasketItemEntity(movieId);
            item.setBasket(basket);
            basket.getBasketItems().add(item);
        }
        // If the movie is already in the basket.
        else {
            log.info("Adding item that already exists.");
            Optional<BasketItemEntity> item = basket.getBasketItems().stream()
                    .filter(i -> i.getItemId().equals(movieId))
                    .max(Comparator.comparing(BasketItemEntity::getReservedUntil));

            if (item.isEmpty()) throw new IllegalStateException("Item should be in basket, but is not found.");
            var itemEntity = item.get();

            // If the movie is expired, try to reserve it again.
            if (itemEntity.getReservedUntil().isBefore(Instant.now())) {
                log.info("This item exists in the basket, but it is expired. Trying to reserve it again.");
                try {
                    stockService.reserveMovie(movieId);
                }
                catch (Exception e) {
                    String message = String.format("Movie with id=%s could not be reserved again, because it is not available in the stock anymore: %s", movieId, e.getMessage());
                    log.error(message);
                    throw new IllegalStateException(message, e);
                }
            }

            // Update expiration date for existing itemEntity in the basket.
            log.info("Update expiration date for existing itemEntity in the basket.");
            basket.getBasketItems().stream().filter(i -> i.getItemId().equals(movieId)).forEach(i -> {
                i.setReservedUntil(Instant.now().plus(ItemValidity));
            });
        }
        // Add a new itemEntity to the basket.
        basket.setValidUntilDate(Instant.now().plus(BasketValidity));
        log.info("Adding new itemEntity to the basket.");
        repo.saveAndFlush(basket);
    }

    public void removeItemFromBasket(String userName, String movieId) {
        log.debug("Removing item from basket for user: {}", userName);
        BasketEntity basket = getOrCreateActiveBasket(userName);
        var didRemove = basket.getBasketItems().removeIf(item -> item.getItemId().equals(movieId));
        if (didRemove)
            stockService.freeUpMovie(movieId);
        basket.setValidUntilDate(Instant.now().plus(BasketValidity));
        repo.saveAndFlush(basket);
    }

    public void removeAllItemsFromBasket(String userName) {
        log.debug("Removing all items from basket for user: {}", userName);
        BasketEntity basket = getOrCreateActiveBasket(userName);
        var allItemIds = basket.getBasketItems().stream().map(BasketItemEntity::getItemId).collect(Collectors.toSet());
        allItemIds.forEach(stockService::freeUpMovie); // this could free up expired movie.
        basket.getBasketItems().clear();
        basket.setValidUntilDate(Instant.now().plus(BasketValidity));
        repo.saveAndFlush(basket);
    }

    public void saveBasketAfterOrdering(BasketEntity basket) {
        log.info("Saving basket after ordering.");
        repo.saveAndFlush(basket);
    }

    private BasketItemEntity createAndPersistBasketItemEntity(String movieId) {
        log.debug("Creating and persisting basket item entity.");
        var entity = new BasketItemEntity();
        entity.setReservedUntil(Instant.now().plus(ItemValidity));
        entity.setItemId(movieId);
        itemRepo.saveAndFlush(entity);
        return entity;
    }

    public Long getUserId(String userName) {
        log.debug("Getting user id for user: {}", userName);
        return userRepo.findByUsername(userName).orElseThrow(() -> new IllegalArgumentException("User doesn't exist!")).getId();
    }
}
