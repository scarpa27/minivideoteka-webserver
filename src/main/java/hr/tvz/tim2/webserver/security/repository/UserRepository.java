package hr.tvz.tim2.webserver.security.repository;

import hr.tvz.tim2.webserver.security.domain.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.isBanned = :banned WHERE u.id = :userId")
    int updateBannedById(@Param("userId") Long userId, @Param("banned") boolean banned);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.isBanned = :banned WHERE u.username = :userName")
    int updateBannedByUsername(@Param("userName") String userName, @Param("banned") boolean banned);

    @Query("SELECT u.isBanned FROM User u WHERE u.id = :userId")
    Boolean findBannedById(@Param("userId") Long userId);

    @Query("SELECT u.isBanned FROM User u WHERE u.username = :userName")
    Boolean findBannedByUsername(@Param("userName") String username);
}