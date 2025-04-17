package hr.tvz.tim2.webserver.stock.logic;

import hr.tvz.tim2.webserver.persistance.Repository;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@org.springframework.stereotype.Repository
public class MStockRepository implements StockRepository {
    private final Map<String, Integer> stocks;

    private final Repository repo;

    public MStockRepository(@Qualifier("MRepository")  Repository repository) {
        stocks = new HashMap<>();
        repo = repository;

        fillFakeStocks();
    }

    public Map<String, Integer> getStock() {
        return stocks;
    }

    public void increaseStock(String movieId) {
        stocks.put(movieId, stocks.get(movieId) + 1);
    }

    public void decreaseStock(String movieId) throws IllegalArgumentException {
        Integer currentStock = stocks.get(movieId);
        if (currentStock == null || currentStock < 1)
            throw new IllegalArgumentException("Stock does not exist");

        stocks.put(movieId, currentStock - 1);
    }



    private void fillFakeStocks() {
        repo.getAllMovies().forEach(movie -> {
            String id = movie.getId();
            Integer random = ThreadLocalRandom.current().nextInt(0, 8);

            stocks.put(id, random);
        });
    }
}
