package hr.tvz.tim2.webserver.stock.logic;

import hr.tvz.tim2.webserver.domain.Movie;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class StockService {
    private final StockDbRepository stockRepo;

    public StockService(@Autowired StockDbRepository stockRepository) {
        this.stockRepo = stockRepository;
    }

    public Map<String, Integer> getAllStocks() {
        Map<String, Integer> ret = new HashMap<>();
        stockRepo.findAll().forEach(s -> ret.put(s.getId(), s.getQuantity()));
        return ret;
    }

    public Optional<StockEntity> getStockEntityById(String id) {
        return stockRepo.findById(id);
    }

    public Integer getStockById(String id) {
        var stock = stockRepo.findById(id);

        if (stock.isEmpty())
            throw new IllegalArgumentException("Stock not found");

        return stock.get().getQuantity();
    }

    @Transactional
    public void reserveMovie(Movie movie) {
        String id = movie.getId();
        reserveMovie(id);
    }

    @Transactional
    public void reserveMovie(String movieId) {
        stockRepo.findById(movieId)
                .ifPresent(stock -> {
                    var quantity = stock.getQuantity();
                    if (quantity <= 0)
                        throw new IllegalStateException("Stock quantity out of range");
                    stock.setQuantity(quantity - 1);
                });
    }

    public void freeUpMovie(String movieId) {
        var stck = stockRepo.findById(movieId);

        stck.ifPresentOrElse(stock -> {
            var quantity = stock.getQuantity();
            stock.setQuantity(quantity + 1);
            stockRepo.save(stock);
        }, () -> {
            var stock = new StockEntity();
            stock.setId(movieId);
            stock.setQuantity(1);
            stockRepo.save(stock);
        });
    }

    public void freeUpMovie(Movie movie) {
        String id = movie.getId();
        freeUpMovie(id);
    }
}