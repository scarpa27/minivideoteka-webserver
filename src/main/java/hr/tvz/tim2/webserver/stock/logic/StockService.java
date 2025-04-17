package hr.tvz.tim2.webserver.stock.logic;

import hr.tvz.tim2.webserver.domain.Movie;
import hr.tvz.tim2.webserver.dto.DtoMapper;
import hr.tvz.tim2.webserver.dto.StockDto;
import hr.tvz.tim2.webserver.persistance.MovieDbRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static hr.tvz.tim2.webserver.dto.DtoMapper.toDto;

@Service
public class StockService {
    private final StockDbRepository stockRepo;
    private final MovieDbRepo movieDbRepo;

    public StockService(@Autowired StockDbRepository stockRepository,
                        @Autowired MovieDbRepo movieDbRepo) {
        this.stockRepo = stockRepository;
        this.movieDbRepo = movieDbRepo;
    }

    public List<StockDto> getAllStocks() {
        return stockRepo.findAll().stream().map(DtoMapper::toDto).toList();
    }

    public Optional<StockEntity> getStockEntityById(String id) {
        return stockRepo.findById(id);
    }

    public StockDto getStockById(String id) {
        var stock = stockRepo.findById(id);

        if (stock.isEmpty()) throw new IllegalArgumentException("Stock not found");

        return toDto(stock.get());
    }

    @Transactional
    public void reserveMovie(Movie movie) {
        String id = movie.getId();
        reserveMovie(id);
    }

    @Transactional
    public void reserveMovie(String movieId) {
        stockRepo.findById(movieId).ifPresent(stock -> {
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

    private void initialSetup() {
        List<Movie> allMovies = movieDbRepo.findAll();
        allMovies.forEach(m -> {
            StockEntity stock = m.getStock();
            if (stock == null) {
                stock = new StockEntity();
                stock.setId(m.getId());
                stock.setQuantity(6);
                stockRepo.save(stock);
            }
            else {
                stock.setQuantity(new Random().nextInt(11));
                stockRepo.save(stock);
            }
        });
    }
}