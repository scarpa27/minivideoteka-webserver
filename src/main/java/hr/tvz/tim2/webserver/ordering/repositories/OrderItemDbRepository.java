package hr.tvz.tim2.webserver.ordering.repositories;

import hr.tvz.tim2.webserver.ordering.entities.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemDbRepository extends JpaRepository<OrderItemEntity, Long> {
    @Query("""
            SELECT COUNT(oi) > 0
            FROM OrderItemEntity oi
            JOIN oi.order o
            WHERE o.user.username = :userName AND oi.itemId = :movieId
            """)
    boolean existsByUserIdAndMovieId(@Param("userName") String userName, @Param("movieId") String movieId);


}
