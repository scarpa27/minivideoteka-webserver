package hr.tvz.tim2.webserver.persistance;

import hr.tvz.tim2.webserver.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyDbRepository extends JpaRepository<Company, String> {
}
