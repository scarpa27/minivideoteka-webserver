package hr.tvz.tim2.webserver.movie.repository;

import hr.tvz.tim2.webserver.movie.entities.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PersonDbRepository extends JpaRepository<PersonEntity, String> {
@Query(value = """
    SELECT c.*, (
        (SELECT COUNT(*) FROM movie_actor_join maj WHERE maj.person_id = c.id) +
        (SELECT COUNT(*) FROM movie_director_join mdj WHERE mdj.person_id = c.id) +
        (SELECT COUNT(*) FROM movie_creator_join mcj WHERE mcj.creator_id = c.id)
    ) AS total_involvement
    FROM creator c
    WHERE c.id IN (
        SELECT DISTINCT maj.person_id FROM movie_actor_join maj
    )
    ORDER BY total_involvement DESC
""", nativeQuery = true)
    List<PersonEntity> findActorsSortByCreated();
}
