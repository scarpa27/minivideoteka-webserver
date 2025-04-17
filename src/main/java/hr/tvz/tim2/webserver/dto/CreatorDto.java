package hr.tvz.tim2.webserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class CreatorDto {
    String id;
    String name;

    List<MovieDto> createdMovies;
    List<MovieDto> starredMovies;
    List<MovieDto> directedMovies;

    @Data @AllArgsConstructor
    static class MovieDto {
        String id;
        String name;
    }
}