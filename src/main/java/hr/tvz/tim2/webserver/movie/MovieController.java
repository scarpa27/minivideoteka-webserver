package hr.tvz.tim2.webserver.movie;

import hr.tvz.tim2.webserver.common.exception.ApiError;
import hr.tvz.tim2.webserver.dto.CreatorDto;
import hr.tvz.tim2.webserver.dto.DtoMapper;
import hr.tvz.tim2.webserver.dto.MovieDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/movies")
@ApiResponse(responseCode = "200")
@ApiResponse(description = "Error", content = @Content(schema = @Schema(implementation = ApiError.class)))
public class MovieController {
    private final MovieService movieService;

    @Autowired
    public MovieController(@Qualifier("movieService") MovieService service) {
        this.movieService = service;
    }

    @GetMapping
    public ResponseEntity<List<MovieDto>> getAllMovies() {
        try {
            return ResponseEntity
                    .ok().body(movieService.getAllMovies().stream()
                                       .map(DtoMapper::toDto).toList());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("multi")
    public ResponseEntity<List<MovieDto>> getSpecificMovies(@RequestParam List<String> ids) {
        try {
            return ResponseEntity.ok().body(movieService.getSpecificListDto(ids));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("one")
    public ResponseEntity<MovieDto> getSpecificMovies(@RequestParam String id) {
        try {
            return ResponseEntity.ok().body(movieService.getSpecificDto(id));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/actors")
    public ResponseEntity<List<CreatorDto>> getAllActors() {
        try {
            return ResponseEntity.ok(movieService.getAllActors().stream().map(DtoMapper::toDto).toList());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("byActor/{actorId}")
    public ResponseEntity<List<MovieDto>> getAllMoviesByActor(@PathVariable String actorId) {
        try {
            return ResponseEntity.ok(movieService.getMoviesByActor(actorId).stream()
                                             .map(DtoMapper::toDto).toList());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("byKeywords/{keywords}")
    public ResponseEntity<List<MovieDto>> getAllMoviesByKeywords(@PathVariable String keywords) {
        try {
            return ResponseEntity.ok(movieService.getFilteredMovies(keywords).stream()
                                             .map(DtoMapper::toDto).toList());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}