package hr.tvz.tim2.webserver.controller;

import hr.tvz.tim2.webserver.dto.ReviewDto;
import hr.tvz.tim2.webserver.review.ReviewCommand;
import hr.tvz.tim2.webserver.review.ReviewService;
import hr.tvz.tim2.webserver.security.user.ApplicationUser;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(@Autowired ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/byMovie/{movieId}")
    public ResponseEntity<?> getReviewsByMovie(@PathVariable String movieId) {
        try {
            List<ReviewDto> all = reviewService.getAllByMovieId(movieId);
            return ResponseEntity.ok().body(all);
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/byUser")
    public ResponseEntity<?> getReviewsByUser(@AuthenticationPrincipal ApplicationUser user) {
        try {
            var all = reviewService.getAllByAuthorId(user.getUsername());
            return ResponseEntity.ok().body(all);
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/allowedReview/{movieId}")
    public ResponseEntity<?> isAllowed(@PathVariable String movieId,
                                       @AuthenticationPrincipal ApplicationUser user) {
        String userName = user.getUsername();
        var isAllowed = reviewService.canUserReviewMovie(userName, movieId);
        return ResponseEntity.ok().body(isAllowed);
    }

    @PutMapping("/put/{movieId}")
    public ResponseEntity<?> addReview(@Valid @RequestBody ReviewCommand command,
                                       @PathVariable String movieId,
                                       @AuthenticationPrincipal ApplicationUser user) {
        String userName = user.getUsername();

        try {
            reviewService.saveOrUpdateReview(userName, movieId, command);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<?> deleteReview(@PathVariable String movieId,
                                          @AuthenticationPrincipal ApplicationUser user) {
        String userName = user.getUsername();
        try {
            reviewService.deleteReview(userName, movieId);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }
}
