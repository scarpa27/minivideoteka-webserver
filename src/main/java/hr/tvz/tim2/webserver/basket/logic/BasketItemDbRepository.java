package hr.tvz.tim2.webserver.basket.logic;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Set;

public interface BasketItemDbRepository extends JpaRepository<BasketItemEntity, Long> {

    Set<BasketItemEntity> findAllByBasketAndReservedUntilBefore(BasketEntity basketId, Instant date);

    Set<BasketItemEntity> findAllByReservedUntilBefore(Instant date);
    void deleteAllByReservedUntilBefore(Instant date);

}
