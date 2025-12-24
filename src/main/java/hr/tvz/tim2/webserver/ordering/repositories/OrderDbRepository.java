package hr.tvz.tim2.webserver.ordering.repositories;

import hr.tvz.tim2.webserver.ordering.entities.OrderEntity;
import hr.tvz.tim2.webserver.security.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderDbRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findByUserAndIsReturnedFalse(User user);

    Optional<OrderEntity> findFirstByUserIdAndIsReturnedFalse(Long userId);

    int countAllByUserIdAndIsReturnedFalse(Long userId);
    int countAllByUserAndIsReturnedFalse(User user);

    List<OrderEntity> findAllByUserId(Long userId);
    List<OrderEntity> findAllByUserIdOrderByOrderDate(Long userId);

}
