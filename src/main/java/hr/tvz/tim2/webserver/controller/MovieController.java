package hr.tvz.tim2.webserver.controller;

import hr.tvz.tim2.webserver.domain.Movie;
import hr.tvz.tim2.webserver.dto.CreatorDto;
import hr.tvz.tim2.webserver.dto.DtoMapper;
import hr.tvz.tim2.webserver.dto.MovieDto;
import hr.tvz.tim2.webserver.service.MovieService;
import hr.tvz.tim2.webserver.stock.logic.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;
    private final StockService stockService;

    @Autowired
    public MovieController(@Qualifier("movieService") MovieService service,StockService stockService) {
        this.movieService = service;
        this.stockService = stockService;
    }

    @GetMapping
    public ResponseEntity<List<MovieDto>> getAllMovies() {
        try {
            var allMovies = movieService.getAllMovies();

            for (Movie movie : allMovies) {
                stockService.freeUpMovie(movie);
            }

            return ResponseEntity
                    .ok().body(movieService.getAllMovies().stream()
                                           .map(DtoMapper::toDto).toList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/actors")
    public ResponseEntity<List<CreatorDto>> getAllActors() {
        try {
            return ResponseEntity.ok(movieService.getAllActors().stream().map(DtoMapper::toDto).toList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("byActor/{actorId}")
    public ResponseEntity<List<MovieDto>> getAllMoviesByActor(@PathVariable String actorId) {
        try {
            return ResponseEntity.ok(movieService.getMoviesByActor(actorId).stream()
                                                 .map(DtoMapper::toDto).toList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("byKeywords/{keywords}")
    public ResponseEntity<List<MovieDto>> getAllMoviesByKeywords(@PathVariable String keywords) {
        try {
            return ResponseEntity.ok(movieService.getFilteredMovies(keywords).stream()
                                                 .map(DtoMapper::toDto).toList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("test/addMovies")
    public ResponseEntity<?> addMovies() {
        try {
            movieService.saveAllFakeMovies();
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok().build();
    }
}