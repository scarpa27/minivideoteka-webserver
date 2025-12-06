package hr.tvz.tim2.webserver.service;

import hr.tvz.tim2.webserver.domain.Movie;
import hr.tvz.tim2.webserver.domain.Person;
import hr.tvz.tim2.webserver.persistance.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Qualifier("movieService")
public class MovieService {
    private final Repository repository;

    @Autowired
    public MovieService(@Qualifier("mockRepository")  Repository repository) {
        this.repository = repository;
    }

    public List<Movie> getAllMovies() {
        return repository.getAllMovies();
    }

    public List<Person> getAllActors() {
        return repository.getAllActors().stream().toList();
    }

    public List<Movie> getMoviesByActor(String actorId) {
        return repository.getAllMoviesByActor(actorId);
    }
}
