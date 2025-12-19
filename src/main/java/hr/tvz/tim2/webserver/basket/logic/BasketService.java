package hr.tvz.tim2.webserver.basket.logic;

import hr.tvz.tim2.webserver.dto.BasketDto;
import hr.tvz.tim2.webserver.security.repository.UserRepository;
import hr.tvz.tim2.webserver.stock.logic.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import static hr.tvz.tim2.webserver.dto.DtoMapper.toDto;

@Service
public class BasketService {
    private final StockService stockService;
    private final BasketDbRepository repo;
    private final BasketItemDbRepository itemRepo;
    private final UserRepository userRepo;

    private static final Duration ItemValidity = Duration.of(15, ChronoUnit.MINUTES);
    private static final Duration BasketValidity = Duration.of(3, ChronoUnit.HOURS);
    private final BasketItemDbRepository basketItemDbRepository;

    public BasketService(@Autowired StockService stockService,
                         @Autowired BasketDbRepository repo,
                         @Autowired BasketItemDbRepository itemRepo,
                         @Autowired UserRepository userRepo,
                         BasketItemDbRepository basketItemDbRepository) {
        this.stockService = stockService;
        this.repo = repo;
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
        this.basketItemDbRepository = basketItemDbRepository;
    }

    public BasketEntity getOrCreateActiveBasket(String userName) {
        Long userId = getUserId(userName);
        var basket = repo.findFirstByUserIdAndValidUntilDateAfterAndStatusNot(userId, Instant.now(), BasketStatus.ORDERED);

        return basket.orElseGet(() -> {
            var created = new BasketEntity();
            userRepo.findById(userId).ifPresentOrElse(created::setUser,
                                                      () -> {throw new IllegalArgumentException("User doesn't exist!");});
            created.setStatus(BasketStatus.ACTIVE);
            created.setValidUntilDate(Instant.now().plusSeconds(60 * 60 *3));
            return repo.save(created);
        });
    }

    public BasketEntity refreshBasket(BasketEntity basket) {
        basket.getBasketItems().forEach(item -> {
            try {
                addItemToBasket(basket, item.getItemId());
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return basket;
    }

    public BasketDto getBasketDto(String userName) {
        BasketEntity entity = getOrCreateActiveBasket(userName);
        return toDto(entity);
    }

    public void addItemToBasket(String userName, String movieId) throws Exception {
        BasketEntity basket = getOrCreateActiveBasket(userName);
        addItemToBasket(basket, movieId);
    }

    public void addItemToBasket(BasketEntity basket, String movieId) throws Exception {
        // If the movie is not already in the basket.
        if (basket.getBasketItems().stream().noneMatch(i -> i.getItemId().equals(movieId))) {
            System.out.println("Adding item that doesn't exist.");
            try {
                stockService.reserveMovie(movieId);
            }
            catch (Exception e) {
                String message = String.format("Movie with id=%s could not be reserved: %s", movieId, e.getMessage());
                System.out.printf(message);
                throw new Exception(message, e);
            }
            BasketItemEntity item = createAndPersistBasketItemEntity(movieId);
            item.setBasket(basket);
            basket.getBasketItems().add(item);
        }
        // If the movie is already in the basket.
        else {
            System.out.println("Adding item that already exists.");
            Optional<BasketItemEntity> item = basket.getBasketItems().stream()
                    .filter(i -> i.getItemId().equals(movieId))
                    .max(Comparator.comparing(BasketItemEntity::getReservedUntil));

            if (item.isEmpty()) throw new IllegalStateException("Item should be in basket, but is not found.");
            var itemEntity = item.get();

            // If the movie is expired, try to reserve it again.
            if (itemEntity.getReservedUntil().isBefore(Instant.now())) {
                System.out.println("This item exists in the basket, but it is expired. Trying to reserve it again.");
                try {
                    stockService.reserveMovie(movieId);
                }
                catch (Exception e) {
                    String message = String.format("Movie with id=%s could not be reserved again, because it is not available in the stock anymore: %s", movieId, e.getMessage());
                    System.out.printf(message);
                    throw new Exception(message, e);
                }
            }

            // Update expiration date for existing itemEntity in the basket.
            System.out.println("Update expiration date for existing itemEntity in the basket.");
            basket.getBasketItems().stream().filter(i -> i.getItemId().equals(movieId)).forEach(i -> {
                i.setReservedUntil(Instant.now().plus(ItemValidity));
            });
        }
        // Add a new itemEntity to the basket.
        basket.setValidUntilDate(Instant.now().plus(BasketValidity));
        System.out.println("Adding new itemEntity to the basket.");
        repo.saveAndFlush(basket);
    }

    public void removeItemFromBasket(String userName, String movieId) {
        BasketEntity basket = getOrCreateActiveBasket(userName);
        var didRemove = basket.getBasketItems().removeIf(item -> item.getItemId().equals(movieId));
        if (didRemove)
            stockService.freeUpMovie(movieId);
        basket.setValidUntilDate(Instant.now().plus(BasketValidity));
        repo.saveAndFlush(basket);
    }

    public void removeAllItemsFromBasket(String userName) {
        BasketEntity basket = getOrCreateActiveBasket(userName);
        var allItemIds = basket.getBasketItems().stream().map(BasketItemEntity::getItemId).collect(Collectors.toSet());
        allItemIds.forEach(stockService::freeUpMovie); // this could free up expired movie.
        basket.getBasketItems().clear();
        basket.setValidUntilDate(Instant.now().plus(BasketValidity));
        repo.saveAndFlush(basket);
    }

    public void saveBasketAfterOrdering(BasketEntity basket) {
        repo.saveAndFlush(basket);
    }

    private BasketItemEntity createAndPersistBasketItemEntity(String movieId) {
        var entity = new BasketItemEntity();
        entity.setReservedUntil(Instant.now().plus(ItemValidity));
        entity.setItemId(movieId);
        itemRepo.saveAndFlush(entity);
        return entity;
    }

    public Long getUserId(String userName) {
        return userRepo.findByUsername(userName).orElseThrow(() -> new IllegalArgumentException("User doesn't exist!")).getId();
    }
}
