package hr.tvz.tim2.webserver.dto;

import hr.tvz.tim2.webserver.basket.BasketEntity;
import hr.tvz.tim2.webserver.membership.MemberEntity;
import hr.tvz.tim2.webserver.membership.MembershipDto;
import hr.tvz.tim2.webserver.movie.entities.CreatorEntity;
import hr.tvz.tim2.webserver.movie.entities.MovieEntity;
import hr.tvz.tim2.webserver.movie.entities.PersonEntity;
import hr.tvz.tim2.webserver.ordering.entities.OrderEntity;
import hr.tvz.tim2.webserver.ordering.entities.OrderItemEntity;
import hr.tvz.tim2.webserver.review.ReviewEntity;
import hr.tvz.tim2.webserver.security.domain.Authority;
import hr.tvz.tim2.webserver.security.domain.User;
import hr.tvz.tim2.webserver.stock.StockEntity;
import hr.tvz.tim2.webserver.wish.WishEntity;
import hr.tvz.tim2.webserver.wish.dto.WishDto;

import java.time.Instant;
import java.util.stream.Collectors;

public class DtoMapper {
    private DtoMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static MovieDto toDto(MovieEntity entity) {
        MovieDto dto = new MovieDto();

        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setReleaseDate(entity.getReleaseDate());
        dto.setCoverImageUrl(entity.getCoverImageUrl());
        dto.setDuration(entity.getDuration());
        dto.setDescription(entity.getDescription());
        dto.setYoutubeTrailer(getYoutubeTrailer(entity));
        dto.setRating(entity.getRating());

        dto.setActors(entity.getActors().stream()
                              .map(a -> new MovieDto.CreatorDto(a.getId(), a.getName())).toList());
        dto.setCreators(entity.getCreators().stream()
                                .map(c -> new MovieDto.CreatorDto(c.getId(), c.getName())).toList());
        dto.setDirectors(entity.getDirectors().stream()
                                 .map(d -> new MovieDto.CreatorDto(d.getId(), d.getName())).toList());
        return dto;
    }

    private static MovieDto.YoutubeTrailer getYoutubeTrailer(MovieEntity entity) {
        var trailerDto = new MovieDto.YoutubeTrailer();
        var youtubeTrailerEntity = entity.getYoutubeTrailer();
        if (entity.getYoutubeTrailer() != null) {
            trailerDto.setVideoId(youtubeTrailerEntity.getVideoId());
            trailerDto.setProvider(youtubeTrailerEntity.getProvider());
            trailerDto.setName(youtubeTrailerEntity.getName());
            trailerDto.setWatchUrl(youtubeTrailerEntity.getWatchUrl());
            trailerDto.setEmbedUrl(youtubeTrailerEntity.getEmbedUrl());
        }
        return trailerDto;
    }

    public static CreatorDto toDto(CreatorEntity creator) {
        CreatorDto dto = new CreatorDto();

        dto.setId(creator.getId());
        dto.setName(creator.getName());
        dto.setCreatedMovies(creator.getCreatedMovies().stream()
                                    .map(m -> new CreatorDto.MovieDto(m.getId(), m.getTitle())).toList());

         if (creator instanceof PersonEntity p) {
             dto.setStarredMovies(p.getStarredMovies().stream()
                                   .map(m -> new CreatorDto.MovieDto(m.getId(), m.getTitle())).toList());
             dto.setDirectedMovies(p.getDirectedMovies().stream()
                                   .map(m -> new CreatorDto.MovieDto(m.getId(), m.getTitle())).toList());
         }

         return dto;
    }

    public static StockDto toDto(StockEntity stock) {
        var dto = new StockDto();
        dto.setMovieId(stock.getMovie().getId());
        dto.setStock(stock.getQuantity());
        return dto;
    }

    public static BasketDto toDto(BasketEntity basket) {
        var dto = new BasketDto();
        var validUntil = basket.getValidUntilDate();
        var itemsDto = basket.getBasketItems()
                .stream().map(item -> new BasketDto.ItemDto(item.getItemId(), item.getReservedUntil()))
                .collect(Collectors.toSet());
        dto.setValidUntil(validUntil);
        dto.setItems(itemsDto);

        return dto;
    }

    public static OrderConfirmDto toDto(OrderEntity o) {
        return new OrderConfirmDto(
                o.getOrderTracking(),
                o.getOrderDate(),
                o.getItemIdList().stream().map(OrderItemEntity::getItemId).collect(Collectors.toSet()),
                o.getIsReturned(),
                o.getReturnTracking(),
                o.getReturnDate()
        );
    }

    public static ReviewDto toDto(ReviewEntity review) {
        var dto = new ReviewDto();
        dto.setAuthor(review.getAuthor().getUsername());
        dto.setDate(review.getDate());
        dto.setText(review.getComment());
        dto.setMovieId(review.getMovie().getId());
        return dto;
    }

    public static MembershipDto toDto(MemberEntity entity) {
        var dto = new MembershipDto();
        dto.setValidUntil(entity.getValidUntil());
        dto.setCardInfo(entity.getCardInfo());
        dto.setShippingInfo(entity.getShippingInfo());

        return dto;
    }

    public static UserDto toDto(User u) {
        var dto = new UserDto();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setAuthorities(u.getAuthorities().stream().map(Authority::getName).collect(Collectors.joining(", ")));

        dto.setOrdersCount(u.getOrders().size());
        dto.setHasActiveOrder(u.getOrders().stream().anyMatch(o -> !o.getIsReturned()));
        dto.setReviewsCount(u.getReviews().size());

        dto.setBanned(u.getIsBanned());
        var membership = u.getMembership();
        if (membership != null) {
            dto.setMember(membership.getValidUntil().isAfter(Instant.now()));
            dto.setMembershipDto(toDto(membership));
        } else
            dto.setMember(false);

        return dto;
    }

    public static WishDto toDto(WishEntity entity) {
        var dto = new WishDto();
        dto.setImdbId(entity.getImdbId());
        dto.setMessage(entity.getMessage());
        dto.setFulfilled(entity.isFulfilled());
        return dto;
    }
}