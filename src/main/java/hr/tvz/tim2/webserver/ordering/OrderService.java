package hr.tvz.tim2.webserver.ordering;

import hr.tvz.tim2.webserver.basket.logic.BasketEntity;
import hr.tvz.tim2.webserver.basket.logic.BasketService;
import hr.tvz.tim2.webserver.service.HistoryService;
import hr.tvz.tim2.webserver.service.MailingService;
import hr.tvz.tim2.webserver.stock.logic.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final BasketService basketService;
    private final MailingService mailingService;
    private final StockService stockService;
    private final HistoryService historyService;


    public OrderService(@Autowired BasketService basketService,
                        @Autowired MailingService mailingService,
                        @Autowired StockService stockService,
                        @Autowired HistoryService historyService) {
        this.basketService = basketService;
        this.mailingService = mailingService;
        this.stockService = stockService;
        this.historyService = historyService;
    }

    public String confirmOrder(String userName) {
        BasketEntity basket = basketService.getOrCreateActiveBasket(userName);
        int basketSize = basket.getBasketItems().size();

        if (basketSize < 1)
            throw new IllegalStateException("No items in the basket");

        basketService.removeAllItemsFromBasket(userName);

        return mailingService.generateTrackingNumber(basketSize);
    }

    public String returnOrder(String userId) {
        List<String> lastOrderMovieIds;
        try {
            lastOrderMovieIds = historyService.getMoviesFromUsersLastOrder(69696969L);
            historyService.markOrderAsReturned(69696969L);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        for (String lastOrderMovieId : lastOrderMovieIds) {
            stockService.freeUpMovie(lastOrderMovieId);
        }

        return mailingService.generateTrackingNumber(0);
    }
}

