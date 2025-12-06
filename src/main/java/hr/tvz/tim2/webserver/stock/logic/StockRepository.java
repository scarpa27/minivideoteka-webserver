package hr.tvz.tim2.webserver.stock.logic;

import java.util.Map;

public interface StockRepository {

    Map<String, Integer> getStock();

    void increaseStock(String movieId);

    void decreaseStock(String movieId) throws IllegalArgumentException;

}
