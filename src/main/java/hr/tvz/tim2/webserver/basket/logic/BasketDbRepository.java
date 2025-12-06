package hr.tvz.tim2.webserver.basket.logic;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

@Transactional
public interface BasketDbRepository extends JpaRepository<BasketEntity, Long> {
}
