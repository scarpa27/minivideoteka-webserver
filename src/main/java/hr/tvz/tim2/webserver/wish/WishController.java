package hr.tvz.tim2.webserver.wish;

import hr.tvz.tim2.webserver.common.exception.ApiError;
import hr.tvz.tim2.webserver.security.user.ApplicationUser;
import hr.tvz.tim2.webserver.wish.dto.WishCountDto;
import hr.tvz.tim2.webserver.wish.dto.WishDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static hr.tvz.tim2.webserver.dto.DtoMapper.toDto;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/wish")
@ApiResponse(responseCode = "200")
@ApiResponse(description = "Error", content = @Content(schema = @Schema(implementation = ApiError.class)))
public class WishController {
    private final WishService wishService;

    public WishController(@Autowired WishService wishService) {
        this.wishService = wishService;
    }

    @PutMapping
    public ResponseEntity<WishDto> addWish(@AuthenticationPrincipal ApplicationUser user,
                                           @RequestBody @Valid WishCommand command) {
        return ResponseEntity.ok(toDto(wishService.addWish(command, user.getUsername())));
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<Void> deleteWish(@PathVariable String movieId,
                                           @AuthenticationPrincipal ApplicationUser user) {
        wishService.deleteWish(movieId, user.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<WishDto>> getWishesForUser(@AuthenticationPrincipal ApplicationUser user) {
        return ResponseEntity.ok().body(wishService.getWishesForUser(user.getUsername()));
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping("/adminAll")
    public ResponseEntity<List<WishCountDto>> getWishes() {
        wishService.getTopWishes();
        return ResponseEntity.ok().body(wishService.getTopWishes());
    }
}
