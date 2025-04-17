package hr.tvz.tim2.webserver.persistance;

import hr.tvz.tim2.webserver.domain.Movie;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

@Transactional
public interface MovieDbRepo extends JpaRepository<Movie, String> {

}
