package hr.tvz.tim2.webserver.movie.repository;

import hr.tvz.tim2.webserver.movie.entities.MovieEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Transactional
public interface MovieDbRepository extends JpaRepository<MovieEntity, String> {
    @Query(value = """
    SELECT m.*
    FROM movie_entity m
    WHERE
      LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
      OR LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
      OR EXISTS (
          SELECT 1
          FROM movie_creator_join mcj
          JOIN creator_entity c ON c.id = mcj.creator_id
          WHERE mcj.movie_id = m.id
            AND LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
    ORDER BY
      CASE WHEN LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) THEN 1 ELSE 0 END DESC,
      CASE WHEN EXISTS (
          SELECT 1
          FROM movie_creator_join mcj
          JOIN creator_entity c ON c.id = mcj.creator_id
          WHERE mcj.movie_id = m.id
            AND LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
      ) THEN 1 ELSE 0 END DESC,
      CASE WHEN LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%')) THEN 1 ELSE 0 END DESC
""", nativeQuery = true)
    List<MovieEntity> findByKeyword(@Param("keyword") String keyword);

    Optional<MovieEntity> findFirstById(String id);

    @Query(value = "SELECT m.id FROM MovieEntity m")
    List<String> findAllIds();

    List<MovieEntity> findByActors_Id(String actorId);
}
