package hr.tvz.tim2.webserver.dto;

import hr.tvz.tim2.webserver.domain.Creator;
import hr.tvz.tim2.webserver.domain.Movie;
import hr.tvz.tim2.webserver.domain.Person;
import hr.tvz.tim2.webserver.stock.logic.StockEntity;

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
        dto.setMovieId(stock.getId());
        dto.setStock(stock.getQuantity());
        return dto;
    }
}