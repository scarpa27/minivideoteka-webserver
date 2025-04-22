package hr.tvz.tim2.webserver.stock;

import hr.tvz.tim2.webserver.movie.entities.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockDbRepository extends JpaRepository<StockEntity, MovieEntity> {
}