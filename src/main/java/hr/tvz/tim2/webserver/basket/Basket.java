package hr.tvz.tim2.webserver.basket;

import lombok.Getter;

import java.util.*;

@Getter
public class Basket {
    private final Long userId;
    private final Long basketId;

    private final Map<String, BasketItem> basketItems;

    public Basket(Long userId) {
        this.userId = userId;
        basketId = generateBasketId(userId);
        this.basketItems = new HashMap<>();
    }

    public void addItem(BasketItem basketItem) {
        basketItems.put(basketItem.getItemId(), basketItem);
    }

    public void addItem(String itemId) {
        addItem(new BasketItem(itemId));
    }

    public void removeItem(BasketItem basketItem) {
        basketItems.remove(basketItem.getItemId());
    }

    public void removeItem(String itemId) {
        basketItems.remove(itemId);
    }

    public void clearItems() {
        basketItems.clear();
    }

    private static Long generateBasketId(Long userId) {
        long time = System.currentTimeMillis() % 1_000_000_000L;
        return (userId << 30) | time;
    }
}
