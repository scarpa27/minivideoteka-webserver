package hr.tvz.tim2.webserver.admin;

import hr.tvz.tim2.webserver.dto.MovieDto;
import hr.tvz.tim2.webserver.movie.MovieService;
import hr.tvz.tim2.webserver.stock.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    private final MovieService movieService;
    private final StockService stockService;

    public AdminController(@Autowired AdminService adminService,
                           @Autowired MovieService movieService,
                           @Autowired StockService stockService) {
        this.adminService = adminService;
        this.movieService = movieService;
        this.stockService = stockService;
    }


    @PostMapping("/movie")
    public ResponseEntity<?> saveOrUpdateMovie(@RequestBody MovieDto dto) {
        try {
            movieService.createOrUpdate(dto);
            return ResponseEntity.ok().body(movieService.getSpecificDto(dto.getId()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            var users = adminService.getAllUsers();
            return ResponseEntity.ok().body(users);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    @PostMapping("/ban/id/{userId}")
    public ResponseEntity<?> banUserById(@PathVariable Long userId) {
        adminService.banUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ban/username/{username}")
    public ResponseEntity<?> banUserByUsername(@PathVariable String username) {
        adminService.banUser(username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unban/id/{userId}")
    public ResponseEntity<?> unbanUserById(@PathVariable Long userId) {
        adminService.unbanUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unban/username/{username}")
    public ResponseEntity<?> unbanUserByUsername(@PathVariable String username) {
        adminService.unbanUser(username);
        return ResponseEntity.ok().build();
    }
}
