package hr.tvz.tim2.webserver.stock.logic;

import hr.tvz.tim2.webserver.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockDbRepository extends JpaRepository<StockEntity, String> {
    Optional<StockEntity> findByMovie(Movie movie);
}