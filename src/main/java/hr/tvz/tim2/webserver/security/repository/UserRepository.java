package hr.tvz.tim2.webserver.security.repository;

import hr.tvz.tim2.webserver.security.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}