package hr.tvz.tim2.webserver.service;

import hr.tvz.tim2.webserver.basket.logic.Basket;
import hr.tvz.tim2.webserver.basket.logic.BasketItem;
import hr.tvz.tim2.webserver.ordering.Order;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class HistoryService {
    private final List<Order> orderHistory;
    private final List<Order> activeOrders;

    public HistoryService() {
        this.orderHistory = new ArrayList<>();
        this.activeOrders = new ArrayList<>();
    }

    public void addCompletedOrder(Basket basket) {
        var userId = basket.getUserId();
        List<String> basketItems = basket.getBasketItems().keySet().stream().toList();
        var date = Instant.now();

        if (!canUserOrder(userId))
            throw new IllegalStateException("Can't order this user. Old order still not returned!");

        Order order = new Order(userId, basketItems, date);
        orderHistory.add(order);
        activeOrders.add(order);
    }

    public void markOrderAsReturned(Long userId) {
        activeOrders.removeIf(order -> order.userId.equals(userId));
    }

    public boolean canUserOrder(Long userId) {
        return activeOrders.stream().noneMatch(order -> order.userId.equals(userId));
    }

    public List<String> getMoviesFromUsersLastOrder(Long userId) {
        Optional<Order> lastOrder = activeOrders.stream().filter(order -> Objects.equals(order.userId, userId))
                                                .max(Comparator.comparing(order -> order.orderDate))
                                                .stream().findFirst();

        if (lastOrder.isPresent()) {
            return lastOrder.get().itemIdList;
        }

        throw new IllegalStateException("Can't find active orders from user with id " + userId);
    }
}
