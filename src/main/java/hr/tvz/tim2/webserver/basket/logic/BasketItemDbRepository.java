package hr.tvz.tim2.webserver.basket.logic;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketItemDbRepository extends JpaRepository<BasketItemEntity, Long> {
}
