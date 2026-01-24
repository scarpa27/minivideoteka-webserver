package hr.tvz.tim2.webserver.movie.domain;

import hr.tvz.tim2.webserver.movie.entities.MovieEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MovieSpecs {
    private MovieSpecs() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<MovieEntity> withFilter(MovieFilter f) {
        return (root, query, criteria) -> {
            var predicates = new ArrayList<Predicate>();

            if (f.getRatingMin() != null) {
                predicates.add(criteria.greaterThanOrEqualTo(root.get("rating"), f.getRatingMin()));
            }
            if (f.getRatingMax() != null) {
                predicates.add(criteria.lessThanOrEqualTo(root.get("rating"), f.getRatingMax()));
            }
            if (f.getYearFrom() != null && f.getYearTo() > 0) {
                LocalDate start = LocalDate.of(f.getYearFrom(), 1, 1)
                        .atStartOfDay(ZoneOffset.UTC)
                        .toLocalDate();
                predicates.add(criteria.greaterThanOrEqualTo(root.get("releaseDate"), start));
            }
            if (f.getYearTo() != null && f.getYearTo() > 0) {
                LocalDate endExclusive = LocalDate.of(f.getYearTo() + 1, 1, 1)
                        .atStartOfDay(ZoneOffset.UTC)
                        .toLocalDate();
                predicates.add(criteria.lessThan(root.get("releaseDate"), endExclusive));
            }

            if (f.getQ() != null && !f.getQ().isBlank()) {
                String pattern = "%" + f.getQ().trim().toLowerCase(Locale.ROOT) + "%";

                Expression<String> title = criteria.lower(criteria.coalesce(root.get("title").as(String.class), ""));
                Expression<String> description = criteria.lower(criteria.coalesce(root.get("description").as(String.class), ""));

                Predicate titleLike = criteria.like(title, pattern);
                Predicate descLike  = criteria.like(description, pattern);

                Objects.requireNonNull(query, "CriteriaQuery is required");

                // actors
                Subquery<Integer> actorsSq = query.subquery(Integer.class);
                Root<MovieEntity> aMovie = actorsSq.correlate(root);
                Join<MovieEntity, ?> a = aMovie.join("actors");
                actorsSq.select(criteria.literal(1))
                        .where(criteria.like(criteria.lower(a.get("name").as(String.class)), pattern));
                Predicate actorExists = criteria.exists(actorsSq);

                // directors
                Subquery<Integer> directorsSq = query.subquery(Integer.class);
                Root<MovieEntity> dMovie = directorsSq.correlate(root);
                Join<MovieEntity, ?> d = dMovie.join("directors");
                directorsSq.select(criteria.literal(1))
                        .where(criteria.like(criteria.lower(d.get("name").as(String.class)), pattern));
                Predicate directorExists = criteria.exists(directorsSq);

                Predicate personExists = criteria.or(actorExists, directorExists);

                predicates.add(criteria.or(titleLike, descLike, personExists));

                query.distinct(true);

                applyRelevanceOrdering(query, criteria, root, titleLike, personExists, descLike);
            }

            return criteria.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static void applyRelevanceOrdering(CriteriaQuery<?> query,
                                               CriteriaBuilder cb,
                                               Root<MovieEntity> root,
                                               Predicate titleLike,
                                               Predicate personExists,
                                               Predicate descLike) {
        if (query.getResultType() == Long.class) return;

        Expression<Integer> titleScore  = cb.<Integer>selectCase().when(titleLike, 1).otherwise(0);
        Expression<Integer> personScore = cb.<Integer>selectCase().when(personExists, 1).otherwise(0);
        Expression<Integer> descScore   = cb.<Integer>selectCase().when(descLike, 1).otherwise(0);

        query.orderBy(cb.desc(titleScore),
                      cb.desc(personScore),
                      cb.desc(descScore),
                      cb.asc(root.get("id")));
    }
}