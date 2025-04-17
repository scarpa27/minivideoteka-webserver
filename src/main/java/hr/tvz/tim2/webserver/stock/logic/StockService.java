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

//        initialSetup();
    }

    public List<StockDto> getAllStocksDto() {
        return stockRepo.findAll().stream().map(DtoMapper::toDto).toList();
    }

    public Optional<StockEntity> getStockEntityByMovie(Movie id) {
        return stockRepo.findById(id);
    }

    public StockDto getStockDtoById(String movieId) {
        var movie = getMovieById(movieId);
        var stock = movie.getStock();

        return toDto(stock);
    }

    @Transactional
    public void reserveMovie(Movie movie) {
        String id = movie.getId();
        reserveMovie(id);
    }

    @Transactional
    public void reserveMovie(String movieId) {
        StockEntity stock = getOrCreateStockByMovieId(movieId);
        int quantity = stock.getQuantity();
        if (quantity <= 0)
            throw new IllegalStateException("Stock quantity out of range");
        stock.setQuantity(quantity - 1);
    }

    @Transactional
    public void freeUpMovie(String movieId) {
        var stock = getOrCreateStockByMovieId(movieId);
        int quantity = stock.getQuantity();
        if (quantity <= 0)
            throw new IllegalStateException("Stock quantity out of range");
        stock.setQuantity(quantity - 1);
    }

    @Transactional
    public void freeUpMovie(Movie movie) {
        String id = movie.getId();
        freeUpMovie(id);
    }

    private Movie getMovieById(String id) {
        Optional<Movie> movie = movieDbRepo.findById(id);
        return movie.orElseThrow();
    }

    private StockEntity getOrCreateStockByMovieId(String id) {
        Movie movie = getMovieById(id);
        var stock = movie.getStock();
        if (stock == null) {
            stock = new StockEntity();
            stock.setMovie(movie);
            stock.setQuantity(0);
            movie.setStock(stock);
            movieDbRepo.save(movie);
        }
        return stock;
    }

    private void initialSetup() {
        List<Movie> allMovies = movieDbRepo.findAll();
        allMovies.forEach(m -> {
            StockEntity stock = m.getStock();
            if (stock == null)
                stock = new StockEntity();
            stock.setMovie(m);
            stock.setQuantity(new Random().nextInt(11));
            m.setStock(stock);
            movieDbRepo.save(m);
        });
    }
}