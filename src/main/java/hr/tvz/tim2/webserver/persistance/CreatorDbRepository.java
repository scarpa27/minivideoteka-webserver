package hr.tvz.tim2.webserver.persistance;

import hr.tvz.tim2.webserver.domain.Company;
import hr.tvz.tim2.webserver.domain.Creator;
import hr.tvz.tim2.webserver.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreatorDbRepository extends JpaRepository<Creator, String> {
}


