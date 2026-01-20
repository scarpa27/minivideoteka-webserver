package hr.tvz.tim2.webserver.admin;

import hr.tvz.tim2.webserver.common.exception.ApiError;
import hr.tvz.tim2.webserver.dto.MovieDto;
import hr.tvz.tim2.webserver.dto.UserDto;
import hr.tvz.tim2.webserver.movie.MovieService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/admin")
@Secured({"ROLE_ADMIN"})
@ApiResponse(responseCode = "200")
@ApiResponse(description = "Error", content = @Content(schema = @Schema(implementation = ApiError.class)))
public class AdminController {
    private final AdminService adminService;
    private final MovieService movieService;

    public AdminController(@Autowired AdminService adminService,
                           @Autowired MovieService movieService) {
        this.adminService = adminService;
        this.movieService = movieService;
        log.debug("AdminController created");
    }

    @PostMapping("/movie")
    public ResponseEntity<MovieDto> saveOrUpdateMovie(@RequestBody MovieDto dto) {
        log.debug("Saving movie with id: {}", dto.getId());
        movieService.createOrUpdate(dto);
        return ResponseEntity.ok().body(movieService.getSpecificDto(dto.getId()));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.debug("Getting all users");
        List<UserDto> users = adminService.getAllUsers();
        return ResponseEntity.ok().body(users);
    }

    @PostMapping("/ban/id/{userId}")
    public ResponseEntity<Void> banUserById(@PathVariable Long userId) {
        log.debug("Banning user with id: {}", userId);
        adminService.banUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ban/username/{username}")
    public ResponseEntity<Void> banUserByUsername(@PathVariable String username) {
        log.debug("Banning user with username: {}", username);
        adminService.banUser(username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unban/id/{userId}")
    public ResponseEntity<Void> unbanUserById(@PathVariable Long userId) {
        log.debug("Unbanning user with id: {}", userId);
        adminService.unbanUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unban/username/{username}")
    public ResponseEntity<Void> unbanUserByUsername(@PathVariable String username) {
        log.debug("Unbanning user with username: {}", username);
        adminService.unbanUser(username);
        return ResponseEntity.ok().build();
    }
}
