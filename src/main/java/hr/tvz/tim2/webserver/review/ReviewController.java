package hr.tvz.tim2.webserver.review;

import hr.tvz.tim2.webserver.common.exception.ApiError;
import hr.tvz.tim2.webserver.dto.ReviewDto;
import hr.tvz.tim2.webserver.security.user.ApplicationUser;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/reviews")
@ApiResponse(responseCode = "200")
@ApiResponse(description = "Error", content = @Content(schema = @Schema(implementation = ApiError.class)))
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(@Autowired ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/byMovie/{movieId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByMovie(@PathVariable String movieId) {
        var all = reviewService.getAllByMovieId(movieId);
        return ResponseEntity.ok().body(all);
    }

    @GetMapping("/byUser")
    public ResponseEntity<List<ReviewDto>> getReviewsByUser(@AuthenticationPrincipal ApplicationUser user) {
        var all = reviewService.getAllByAuthorId(user.getUsername());
        return ResponseEntity.ok().body(all);
    }

    @GetMapping("/allowedReview/{movieId}")
    public ResponseEntity<Boolean> isAllowed(@PathVariable String movieId,
                                             @AuthenticationPrincipal ApplicationUser user) {
        String userName = user.getUsername();
        var isAllowed = reviewService.canUserReviewMovie(userName, movieId);
        return ResponseEntity.ok().body(isAllowed);
    }

    @PutMapping("/put/{movieId}")
    public ResponseEntity<Void> addReview(@Valid @RequestBody ReviewCommand command,
                                          @PathVariable String movieId,
                                          @AuthenticationPrincipal ApplicationUser user) {
        String userName = user.getUsername();
        reviewService.saveOrUpdateReview(userName, movieId, command);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<Void> deleteReview(@PathVariable String movieId,
                                             @AuthenticationPrincipal ApplicationUser user) {
        String userName = user.getUsername();
        reviewService.deleteReview(userName, movieId);
        return ResponseEntity.ok().build();
    }
}
