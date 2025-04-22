package hr.tvz.tim2.webserver.review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewDbRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findAllByMovieId(String movieId);

    List<ReviewEntity> findAllByAuthorId(Long userId);

    Optional<ReviewEntity> findByAuthorIdAndMovieId(Long userId, String movieId);

    void deleteAllByAuthorIdAndMovieId(Long userId, String movieId);
}
