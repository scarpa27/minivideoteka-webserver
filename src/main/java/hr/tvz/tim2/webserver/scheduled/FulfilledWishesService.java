package hr.tvz.tim2.webserver.scheduled;

import hr.tvz.tim2.webserver.movie.repository.MovieDbRepository;
import hr.tvz.tim2.webserver.wish.WishDbRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FulfilledWishesService {
    private final WishDbRepository wishDbRepository;
    private final MovieDbRepository movieDbRepository;


    public FulfilledWishesService(@Autowired WishDbRepository wishDbRepository,
                                  @Autowired MovieDbRepository movieDbRepository) {
        this.wishDbRepository = wishDbRepository;
        this.movieDbRepository = movieDbRepository;
    }

    @Scheduled(fixedRate = 5 * 60 * 1000, initialDelay = 45 * 1000)
    public void markWishesAsFulfilled () {
        var ids = movieDbRepository.findAllIds();
        int updateCount = wishDbRepository.updateFulfilledWhereImdbId(ids);
        log.debug("Updated {} wishes as fulfilled", updateCount);
    }
}
