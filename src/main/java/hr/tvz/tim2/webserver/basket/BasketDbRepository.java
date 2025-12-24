package hr.tvz.tim2.webserver.basket;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Transactional
public interface BasketDbRepository extends JpaRepository<BasketEntity, Long> {
    Optional<BasketEntity> findFirstByUserIdAndStatusAndValidUntilDateAfter(Long userId, BasketStatus status, Instant date);
    Optional<BasketEntity> findFirstByUserIdAndValidUntilDateAfterAndStatusNot(Long userId, Instant date, BasketStatus status);

    List<BasketEntity> findAllByValidUntilDateBefore(Instant date);
    void deleteAllByValidUntilDateBeforeAndStatusNot(Instant date, BasketStatus status);
}
