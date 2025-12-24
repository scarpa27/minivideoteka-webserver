package hr.tvz.tim2.webserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Duration;
import java.util.List;
import java.util.Date;

@Data
public class MovieDto {
    String id;
    String title;
    List<CreatorDto> actors;
    List<CreatorDto> creators;
    List<CreatorDto> directors;
    Date releaseDate;
    String coverImageUrl;
    Duration duration;
    String description;

    @Data @AllArgsConstructor
    public static class CreatorDto {
        String id;
        String name;
    }
}