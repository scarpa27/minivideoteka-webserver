package hr.tvz.tim2.webserver.ordering;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDbRepository extends JpaRepository<OrderEntity, Long> {
}
