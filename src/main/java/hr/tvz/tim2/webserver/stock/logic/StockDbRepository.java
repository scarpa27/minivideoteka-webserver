package hr.tvz.tim2.webserver.stock.logic;

import hr.tvz.tim2.webserver.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockDbRepository extends JpaRepository<StockEntity, Movie> {
}