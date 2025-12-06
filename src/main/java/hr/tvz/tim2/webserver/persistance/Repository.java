package hr.tvz.tim2.webserver.persistance;

import hr.tvz.tim2.webserver.domain.Movie;
import hr.tvz.tim2.webserver.domain.Person;

import java.util.List;
import java.util.Set;

public interface Repository {
    List<Movie> getAllMovies();

    Set<Person> getAllActors();

    List<Movie> getAllMoviesByActor(String actorId);

    List<Movie> getFilteredMovies(String keyword);
}
