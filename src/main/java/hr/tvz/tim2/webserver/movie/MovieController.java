package hr.tvz.tim2.webserver.movie;

import hr.tvz.tim2.webserver.common.exception.ApiError;
import hr.tvz.tim2.webserver.dto.CreatorDto;
import hr.tvz.tim2.webserver.dto.DtoMapper;
import hr.tvz.tim2.webserver.dto.MovieDto;
import hr.tvz.tim2.webserver.movie.domain.MovieFilter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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
        log.debug("MovieController created");
    }

    /**
     * @deprecated in favour of {@link MovieController#getMovies(MovieFilter, Pageable)}
     */
    @Deprecated(since = "2026-01-24", forRemoval = false)
    @GetMapping
    public ResponseEntity<List<MovieDto>> getAllMovies() {
        log.debug("Getting all movies");
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
        log.debug("Getting movies with ids: {}", ids);
        try {
            return ResponseEntity.ok().body(movieService.getSpecificListDto(ids));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("one")
    public ResponseEntity<MovieDto> getSpecificMovies(@RequestParam String id) {
        log.debug("Getting movie with id: {}", id);
        try {
            return ResponseEntity.ok().body(movieService.getSpecificDto(id));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/actors")
    public ResponseEntity<List<CreatorDto>> getAllActors() {
        log.debug("Getting all actors");
        try {
            return ResponseEntity.ok(movieService.getAllActors().stream().map(DtoMapper::toDto).toList());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("byActor/{actorId}")
    public ResponseEntity<List<MovieDto>> getAllMoviesByActor(@PathVariable String actorId) {
        log.debug("Getting all movies by actor with id: {}", actorId);
        try {
            return ResponseEntity.ok(movieService.getMoviesByActor(actorId).stream()
                                             .map(DtoMapper::toDto).toList());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * @deprecated in favour of {@link MovieController#getMovies(MovieFilter, Pageable)}
     */
    @Deprecated(since = "2026-01-24", forRemoval = false)
    @GetMapping("byKeywords/{keywords}")
    public ResponseEntity<List<MovieDto>> getAllMoviesByKeywords(@PathVariable String keywords) {
        log.debug("Getting all movies by keywords: {}", keywords);
        try {
            return ResponseEntity.ok(movieService.getFilteredMovies(keywords).stream()
                                             .map(DtoMapper::toDto).toList());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("pageable")
    public ResponseEntity<PagedModel<MovieDto>> getMovies(@ModelAttribute MovieFilter filter,
                                                          @PageableDefault(size = 20) Pageable pageable) {
        int maxSize = 100;
        if (pageable.getPageSize() > maxSize) {
            pageable = PageRequest.of(pageable.getPageNumber(), maxSize, pageable.getSort());
        }

        Sort sort = pageable.getSort().and(Sort.by("id").ascending());
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return ResponseEntity.ok(new PagedModel<>(movieService.getMovies(filter, pageable)));
    }
}