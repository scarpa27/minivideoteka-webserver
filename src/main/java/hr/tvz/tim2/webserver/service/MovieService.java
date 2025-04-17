package hr.tvz.tim2.webserver.service;

import hr.tvz.tim2.webserver.domain.Movie;
import hr.tvz.tim2.webserver.domain.Person;
import hr.tvz.tim2.webserver.persistance.MovieDbRepo;
import hr.tvz.tim2.webserver.persistance.Repository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Qualifier("movieService")
@Setter @Getter
public class MovieService {
    private final Repository repository;

    private final MovieDbRepo movieDbRepo;

    @Autowired
    public MovieService(@Qualifier("MRepository") Repository repository,
                        @Autowired MovieDbRepo movieDbRepo) {
        this.repository = repository;
        this.movieDbRepo = movieDbRepo;

//        movieDbRepo.saveAllAndFlush(getAllMovies());
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

    public List<Movie> getFilteredMovies(String keyword) {
        return repository.getFilteredMovies(keyword);
    }

    public void saveAllFakeMovies() {
        movieDbRepo.saveAllAndFlush(getAllMovies());

    }
}
