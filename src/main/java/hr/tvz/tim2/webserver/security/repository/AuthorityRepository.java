package hr.tvz.tim2.webserver.security.repository;

import hr.tvz.tim2.webserver.security.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Optional<Authority> findByName(String roleUser);
}