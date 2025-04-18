package hr.tvz.tim2.webserver.controller;


import hr.tvz.tim2.webserver.service.MovieService;
import hr.tvz.tim2.webserver.stock.logic.StockService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/common")
public class CommonController {
    private final MovieService movieService;
    private final StockService stockService;

    public CommonController(@Autowired MovieService movieService,
                            @Autowired StockService stockService) {
        this.movieService = movieService;
        this.stockService = stockService;
    }

    @GetMapping("time")
    public ResponseEntity<Instant> getServerTime() {
        return ResponseEntity.ok(Instant.now());
    }

    @GetMapping("setupTestEnv")
    @Operation(
            summary = "Initial database populate",
            description = """
                    This will populate database with 250 movies and hook up all creators.
                    It will also randomly generate stock for each movie with between 0 and 10 items in stock.
                    No users will be created, but you should also be able to register yourself.
                    Once you are registered, you need to login and copy token from response body to upper right menu marked as "Authorize".
                    Some endpoints allowed unauthenticated access. You can access this site and any "/movie" endpoint.
                    If you want to manipulate something, database is available at "/h2", user is "sa", password is empty."""
    )
    public ResponseEntity<?> setupTestEnv() {
        try {
            movieService.setUpMovies();
            stockService.initialSetup();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }
}
