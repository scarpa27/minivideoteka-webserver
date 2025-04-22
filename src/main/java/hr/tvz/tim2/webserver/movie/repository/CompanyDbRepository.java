package hr.tvz.tim2.webserver.movie.repository;

import hr.tvz.tim2.webserver.movie.entities.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyDbRepository extends JpaRepository<CompanyEntity, String> {
}
