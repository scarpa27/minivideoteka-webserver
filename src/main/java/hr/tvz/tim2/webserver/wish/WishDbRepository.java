package hr.tvz.tim2.webserver.wish;

import hr.tvz.tim2.webserver.wish.dto.WishCountDto;
import hr.tvz.tim2.webserver.wish.dto.WishDto;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WishDbRepository extends JpaRepository<WishEntity, Long> {
    boolean existsByUsernameAndImdbId(String username, String movieId);

    @Query("""
            SELECT new hr.tvz.tim2.webserver.wish.dto.WishCountDto(w.imdbId, COUNT(w), w.fulfilled)
            FROM WishEntity w
            WHERE w.fulfilled = false
            GROUP BY w.imdbId
            ORDER BY COUNT(w) DESC""")
    List<WishCountDto> findAllUnfulfilledOrderByMovieCount();

    @Query("""
            SELECT new hr.tvz.tim2.webserver.wish.dto.WishCountDto(w.imdbId, COUNT(w), w.fulfilled)
            FROM WishEntity w
            GROUP BY w.imdbId, w.fulfilled
            ORDER BY w.fulfilled ASC, COUNT(w) DESC""")
    List<WishCountDto> findAllOrderByMovieCountUnfulfilledFirst();

    @Query("""
            SELECT new hr.tvz.tim2.webserver.wish.dto.WishDto(w.imdbId, w.message, w.fulfilled)
            FROM WishEntity w
            WHERE w.username = ?1""")
    List<WishDto> findAllByUsernameOrderByMovieCount(String username);

    void deleteAllByUsernameAndImdbId(String username, String imdbId);

    void deleteAllByImdbId(String imdbId);

    @Transactional
    @Modifying
    @Query("""
            UPDATE WishEntity w
            SET w.fulfilled = TRUE
            WHERE w.imdbId IN :imdbIds AND w.fulfilled = FALSE""")
    int updateFulfilledWhereImdbId(@Param("imdbIds") List<String> imdbIds);
}
