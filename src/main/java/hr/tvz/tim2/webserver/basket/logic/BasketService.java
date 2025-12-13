package hr.tvz.tim2.webserver.basket.logic;

import hr.tvz.tim2.webserver.security.repository.UserRepository;
import hr.tvz.tim2.webserver.stock.logic.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
public class BasketService {
    private final Map<Long, Basket> userBasketMap;
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
        userBasketMap = new HashMap<Long, Basket>();
        this.stockService = stockService;
        this.repo = repo;
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
    }

    public BasketEntity getOrCreateActiveBasket(String userName) {
        Long userId = getUserId(userName);
        var basket = repo.findFirstByUserIdAndStatusAndValidUntilDateAfter(userId, BasketStatus.ACTIVE, Instant.now());

        return basket.orElseGet(() -> {
            var created = new BasketEntity();
            userRepo.findById(userId).ifPresentOrElse(created::setUser,
                                                      () -> {throw new IllegalArgumentException("User doesn't exist!");});
            created.setStatus(BasketStatus.ACTIVE);
            created.setValidUntilDate(Instant.now().plusSeconds(60 * 60 *3));
            return repo.save(created);
        });
    }

    public Basket createOrGetBasketForUser(Long userId) {
        return userBasketMap.computeIfAbsent(userId, Basket::new);
    }

    public void addItemToBasket(String userName, String movieId) throws Exception {
        try {
            stockService.reserveMovie(movieId);
        }
        catch (Exception e) {
            String message = String.format("Movie with id=%s could not be reserved: %s", movieId, e.getMessage());
            System.out.printf(message);

            throw new Exception(message, e);
        }
        BasketEntity basket = getOrCreateActiveBasket(userName);
        BasketItemEntity item = createAndPersistBasketItemEntity(movieId);
        basket.getBasketItems().add(item);
        basket.setValidUntilDate(Instant.now().plus(BasketValidity));
        repo.saveAndFlush(basket);
    }

    public void removeItemFromBasket(Long userId, String movieId) {
        stockService.freeUpMovie(movieId);

        Basket basket = createOrGetBasketForUser(userId);
        basket.removeItem(movieId);
    }

    public void removeAllItemsFromBasket(Long userId) {
        Basket basket = createOrGetBasketForUser(userId);

        Map<String, BasketItem> allItems = basket.getBasketItems();
        allItems.keySet().forEach(stockService::freeUpMovie);

        basket.clearItems();
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
