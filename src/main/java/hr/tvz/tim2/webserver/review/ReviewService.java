package hr.tvz.tim2.webserver.review;

import hr.tvz.tim2.webserver.dto.DtoMapper;
import hr.tvz.tim2.webserver.dto.ReviewDto;
import hr.tvz.tim2.webserver.ordering.repositories.OrderItemDbRepository;
import hr.tvz.tim2.webserver.movie.repository.MovieDbRepository;
import hr.tvz.tim2.webserver.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ReviewService {
    private final OrderItemDbRepository orderItemDbRepository;
    private final ReviewDbRepository reviewDbRepository;
    private final UserRepository userDbRepository;
    private final MovieDbRepository movieDbRepository;

    public ReviewService(@Autowired OrderItemDbRepository orderItemDbRepository,
                         @Autowired ReviewDbRepository reviewDbRepository,
                         @Autowired UserRepository userDbRepository,
                         @Autowired MovieDbRepository movieDbRepository) {
        this.orderItemDbRepository = orderItemDbRepository;
        this.reviewDbRepository = reviewDbRepository;
        this.userDbRepository = userDbRepository;
        this.movieDbRepository = movieDbRepository;
    }

    public boolean canUserReviewMovie(String userName, String movieId) {
        return orderItemDbRepository.existsByUserIdAndMovieId(userName, movieId);
    }

    public void saveOrUpdateReview(String userName, String movieId, ReviewCommand command) {
        if(!canUserReviewMovie(userName, movieId)) {
            throw new IllegalArgumentException("User can't review this movie because it was never ordered before!");
        }

        var existingReview = reviewDbRepository.findByAuthorIdAndMovieId(getUserId(userName), movieId);

        var entity = existingReview.orElseGet(() -> createNewPartialReviewEntity(movieId, userName));

        if (!command.getText().equals(entity.getComment()))
            entity.setDate(Instant.now());

        entity.setComment(command.getText());
        reviewDbRepository.saveAndFlush(entity);
    }

    @Transactional
    public void deleteReview(String userName, String movieId) {
        reviewDbRepository.deleteAllByAuthorIdAndMovieId(getUserId(userName), movieId);
        reviewDbRepository.flush();
    }

    public List<ReviewDto> getAllByMovieId(String movieId) {
        return  reviewDbRepository.findAllByMovieId(movieId).stream().map(DtoMapper::toDto).toList();
    }

    public List<ReviewDto> getAllByAuthorId(String userName) {
        return  reviewDbRepository.findAllByAuthorId(getUserId(userName)).stream().map(DtoMapper::toDto).toList();
    }

    private ReviewEntity createNewPartialReviewEntity(String movieId, String userName) {
        var entity = new ReviewEntity();
        var user = userDbRepository.getReferenceById(getUserId(userName));
        var movie = movieDbRepository.getReferenceById(movieId);

        entity.setAuthor(user);
        entity.setMovie(movie);
        return entity;
    }

    private Long getUserId(String userName) {
        return userDbRepository.findByUsername(userName).orElseThrow(() -> new IllegalArgumentException("User doesn't exist!"))
                .getId();
    }
}
