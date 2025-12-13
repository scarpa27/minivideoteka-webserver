package hr.tvz.tim2.webserver.basket.logic;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

@Transactional
public interface BasketDbRepository extends JpaRepository<BasketEntity, Long> {
    Optional<BasketEntity> findFirstByUserIdAndStatusAndValidUntilDateAfter(Long userId, BasketStatus status, Instant date);
}
