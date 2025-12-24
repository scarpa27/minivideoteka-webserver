package hr.tvz.tim2.webserver.movie.repository;

import hr.tvz.tim2.webserver.movie.entities.CreatorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreatorDbRepository extends JpaRepository<CreatorEntity, String> {
}


