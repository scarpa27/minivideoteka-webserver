package hr.tvz.tim2.webserver.controller;

import hr.tvz.tim2.webserver.domain.Movie;
import hr.tvz.tim2.webserver.domain.Person;
import hr.tvz.tim2.webserver.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(@Qualifier("movieService") MovieService service) {
        this.movieService = service;
    }

    @GetMapping
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/actors")
    public List<Person> getAllActors() {
        return movieService.getAllActors();
    }

    @GetMapping("byActor/{id}")
    public List<Movie> getAllMoviesByActor(@PathVariable String id) {
        return movieService.getMoviesByActor(id);
    }
}
