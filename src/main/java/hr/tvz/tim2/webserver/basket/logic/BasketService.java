package hr.tvz.tim2.webserver.basket.logic;

import hr.tvz.tim2.webserver.stock.logic.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BasketService {
    private final Map<Long, Basket> userBasketMap;
    private final StockService stockService;

    public BasketService(@Autowired StockService stockService) {
        userBasketMap = new HashMap<Long, Basket>();
        this.stockService = stockService;
    }

    public Basket createOrGetBasketForUser(Long userId) {
        return userBasketMap.computeIfAbsent(userId, Basket::new);
    }

    public void addItemToBasket(Long userId,
                                String movieId) {
        try {
            stockService.reserveMovie(movieId);
        } catch (Exception e) {
            System.out.printf("Movie with id=%s could not be reserved: %s", movieId, e.getMessage());
            throw e;
        }

        Basket basket = createOrGetBasketForUser(userId);
        basket.addItem(movieId);
    }

    public void removeItemFromBasket(Long userId,
                                     String movieId) {
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
}
