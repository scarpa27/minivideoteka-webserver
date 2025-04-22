package hr.tvz.tim2.webserver.dto;

import hr.tvz.tim2.webserver.basket.logic.BasketEntity;
import hr.tvz.tim2.webserver.domain.Creator;
import hr.tvz.tim2.webserver.domain.Movie;
import hr.tvz.tim2.webserver.domain.Person;
import hr.tvz.tim2.webserver.membership.MemberEntity;
import hr.tvz.tim2.webserver.membership.MembershipDto;
import hr.tvz.tim2.webserver.ordering.OrderEntity;
import hr.tvz.tim2.webserver.ordering.OrderItemEntity;
import hr.tvz.tim2.webserver.review.ReviewEntity;
import hr.tvz.tim2.webserver.stock.logic.StockEntity;

import java.util.stream.Collectors;

public class DtoMapper {
    public static MovieDto toDto(Movie movie) {
        MovieDto dto = new MovieDto();

        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setCoverImageUrl(movie.getCoverImageUrl());
        dto.setDuration(movie.getDuration());
        dto.setDescription(movie.getDescription());

        dto.setActors(movie.getActors().stream()
                                .map(a -> new MovieDto.CreatorDto(a.getId(), a.getName())).toList());
        dto.setCreators(movie.getCreators().stream()
                                  .map(c -> new MovieDto.CreatorDto(c.getId(), c.getName())).toList());
        dto.setDirectors(movie.getDirectors().stream()
                                   .map(d -> new MovieDto.CreatorDto(d.getId(), d.getName())).toList());
        return dto;
    }

    public static CreatorDto toDto(Creator creator) {
        CreatorDto dto = new CreatorDto();

        dto.setId(creator.getId());
        dto.setName(creator.getName());
        dto.setCreatedMovies(creator.getCreatedMovies().stream()
                                    .map(m -> new CreatorDto.MovieDto(m.getId(), m.getTitle())).toList());

         if (creator instanceof Person p) {
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
}