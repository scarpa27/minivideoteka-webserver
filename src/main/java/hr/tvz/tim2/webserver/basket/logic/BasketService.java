package hr.tvz.tim2.webserver.basket.logic;

import hr.tvz.tim2.webserver.dto.BasketDto;
import hr.tvz.tim2.webserver.security.repository.UserRepository;
import hr.tvz.tim2.webserver.stock.logic.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
                         @Autowired UserRepository userRepo, BasketItemDbRepository basketItemDbRepository) {
        this.stockService = stockService;
        this.repo = repo;
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
        this.basketItemDbRepository = basketItemDbRepository;
    }

    public BasketEntity getOrCreateActiveBasket(String userName) {
        Long userId = getUserId(userName);
        var basket = repo.findFirstByUserIdAndValidUntilDateAfter(userId, Instant.now());

        return basket.orElseGet(() -> {
            var created = new BasketEntity();
            userRepo.findById(userId).ifPresentOrElse(created::setUser,
                                                      () -> {throw new IllegalArgumentException("User doesn't exist!");});
            created.setStatus(BasketStatus.ACTIVE);
            created.setValidUntilDate(Instant.now().plusSeconds(60 * 60 *3));
            return repo.save(created);
        });
    }

    public BasketDto getBasketDto(String userName) {
        BasketEntity entity = getOrCreateActiveBasket(userName);
        return toDto(entity);
    }

    public void addItemToBasket(String userName, String movieId) throws Exception {
        BasketEntity basket = getOrCreateActiveBasket(userName);
        if (basket.getBasketItems().stream().noneMatch(i -> i.getItemId().equals(movieId)))
            try {
                stockService.reserveMovie(movieId);
            }
            catch (Exception e) {
                String message = String.format("Movie with id=%s could not be reserved: %s", movieId, e.getMessage());
                System.out.printf(message);
                throw new Exception(message, e);
            }
        else {
            basket.getBasketItems().stream().filter(i -> i.getItemId().equals(movieId)).forEach(item -> {
                basketItemDbRepository.delete(item);
                basket.getBasketItems().remove(item);
                basketItemDbRepository.flush();
            });
        }
        BasketItemEntity item = createAndPersistBasketItemEntity(movieId);
        item.setBasket(basket);
        basket.getBasketItems().add(item);
        basket.setValidUntilDate(Instant.now().plus(BasketValidity));
        repo.saveAndFlush(basket);
    }

    public void removeItemFromBasket(String userName, String movieId) {
        stockService.freeUpMovie(movieId);

        BasketEntity basket = getOrCreateActiveBasket(userName);
        basket.getBasketItems().removeIf(item -> item.getItemId().equals(movieId));
        basket.setValidUntilDate(Instant.now().plus(BasketValidity));
        repo.saveAndFlush(basket);
    }

    public void removeAllItemsFromBasket(String userName) {
        BasketEntity basket = getOrCreateActiveBasket(userName);
        var allItemIds = basket.getBasketItems().stream().map(BasketItemEntity::getItemId).collect(Collectors.toSet());
        allItemIds.forEach(stockService::freeUpMovie);
        basket.getBasketItems().clear();
        basket.setValidUntilDate(Instant.now().plus(BasketValidity));
        repo.saveAndFlush(basket);
    }

    private BasketItemEntity createAndPersistBasketItemEntity(String movieId) {
        var entity = new BasketItemEntity();
        entity.setReservedUntil(Instant.now().plus(ItemValidity));
        entity.setItemId(movieId);
        itemRepo.saveAndFlush(entity);
        return entity;
    }

    private Long getUserId(String userName) {
        return userRepo.findByUsername(userName).orElseThrow(() -> new IllegalArgumentException("User doesn't exist!")).getId();
    }
}
