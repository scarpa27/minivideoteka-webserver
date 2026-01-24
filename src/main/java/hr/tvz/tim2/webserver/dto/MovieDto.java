package hr.tvz.tim2.webserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Data
public class MovieDto {
    String id;
    String title;
    List<CreatorDto> actors;
    List<CreatorDto> creators;
    List<CreatorDto> directors;
    LocalDate releaseDate;
    String coverImageUrl;
    Duration duration;
    String description;
    BigDecimal rating;

    YoutubeTrailer youtubeTrailer;

    @Data @AllArgsConstructor
    public static class CreatorDto {
        String id;
        String name;
    }

    @Data @NoArgsConstructor
    public static class YoutubeTrailer {
        String videoId;
        String provider;
        String name;
        String watchUrl;
        String embedUrl;
    }
}