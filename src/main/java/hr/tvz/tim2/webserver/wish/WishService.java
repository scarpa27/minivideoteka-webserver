package hr.tvz.tim2.webserver.wish;

import hr.tvz.tim2.webserver.movie.repository.MovieDbRepository;
import hr.tvz.tim2.webserver.wish.dto.WishCountDto;
import hr.tvz.tim2.webserver.wish.dto.WishDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishService {
    private final WishDbRepository wishDbRepository;
    private final MovieDbRepository movieDbRepository;

    public WishService(@Autowired WishDbRepository wishDbRepository,
                       @Autowired  MovieDbRepository movieDbRepository) {
        this.wishDbRepository = wishDbRepository;
        this.movieDbRepository = movieDbRepository;
    }

    public WishEntity addWish(WishCommand wishCommand, String username) {
        if (movieDbRepository.existsById(wishCommand.getImdbId()))
            throw new IllegalArgumentException("Movie with this ID is already in the database!");

        if (wishDbRepository.existsByUsernameAndImdbId(username, wishCommand.getImdbId()))
            throw new IllegalArgumentException("You already wished for this movie!");

        return wishDbRepository.saveAndFlush(mapToEntity(wishCommand, username));
    }

    public void deleteWish(String username, String imdbId) {
        wishDbRepository.deleteAllByUsernameAndImdbId(username, imdbId);
        wishDbRepository.flush();
    }

    public List<WishDto> getWishesForUser(String username) {
        return wishDbRepository.findAllByUsernameOrderByMovieCount(username);
    }

    // admin
    public List<WishCountDto> getTopWishes() {
        return wishDbRepository.findAllOrderByMovieCountUnfulfilledFirst();
    }

    private WishEntity mapToEntity(WishCommand wishCommand, String username) {
        var imdbId = wishCommand.getImdbId();
        var message = wishCommand.getMessage();

        var entity = new WishEntity();
        entity.setImdbId(imdbId);
        entity.setMessage(message);
        entity.setUsername(username);

        return entity;
    }
}
