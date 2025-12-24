package hr.tvz.tim2.webserver.stock;

import hr.tvz.tim2.webserver.movie.entities.MovieEntity;
import hr.tvz.tim2.webserver.dto.DtoMapper;
import hr.tvz.tim2.webserver.dto.StockDto;
import hr.tvz.tim2.webserver.movie.repository.MovieDbRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static hr.tvz.tim2.webserver.dto.DtoMapper.toDto;

@Service
public class StockService {
    private final StockDbRepository stockRepo;
    private final MovieDbRepository movieDbRepository;

    public StockService(@Autowired StockDbRepository stockRepository,
                        @Autowired MovieDbRepository movieDbRepository) {
        this.stockRepo = stockRepository;
        this.movieDbRepository = movieDbRepository;
    }

    public List<StockDto> getAllStocksDto() {
        return stockRepo.findAll().stream().map(DtoMapper::toDto).toList();
    }

    public StockDto getStockDtoById(String movieId) {
        var movie = getMovieById(movieId);
        var stock = movie.getStock();

        return toDto(stock);
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
        stock.setQuantity(quantity + 1);
    }

    private MovieEntity getMovieById(String id) {
        Optional<MovieEntity> movie = movieDbRepository.findById(id);
        return movie.orElseThrow();
    }

    private StockEntity getOrCreateStockByMovieId(String id) {
        MovieEntity movie = getMovieById(id);
        var stock = movie.getStock();
        if (stock == null) {
            stock = new StockEntity();
            stock.setMovie(movie);
            stock.setQuantity(0);
            movie.setStock(stock);
            movieDbRepository.save(movie);
        }
        return stock;
    }

    public void initialSetup() {
        stockRepo.deleteAll();
        stockRepo.flush();
        List<MovieEntity> allMovies = movieDbRepository.findAll();
        allMovies.forEach(m -> {
            StockEntity stock = m.getStock();
            if (stock == null)
                stock = new StockEntity();
            stock.setMovie(m);
//            stock.setQuantity(new Random().nextInt(11));
            stock.setQuantity(1);
            m.setStock(stock);
            movieDbRepository.save(m);
        });
        movieDbRepository.flush();
    }
}