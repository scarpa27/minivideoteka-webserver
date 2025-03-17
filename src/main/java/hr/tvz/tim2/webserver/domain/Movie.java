package hr.tvz.tim2.webserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import hr.tvz.tim2.webserver.util.deserializer.CreatorDeserializer;
import hr.tvz.tim2.webserver.util.deserializer.DateDeserializer;
import hr.tvz.tim2.webserver.util.deserializer.DurationDeserializer;
import hr.tvz.tim2.webserver.util.deserializer.MovieIdDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Movie {
    @JsonProperty("url") @JsonDeserialize(using = MovieIdDeserializer.class)
    private String id;
    @JsonProperty("actor") @JsonDeserialize(contentUsing = CreatorDeserializer.class)
    private List<Person> actors;
    @JsonProperty("creator")  @JsonDeserialize(contentUsing = CreatorDeserializer.class)
    private List<Creator> creators;
    @JsonProperty("name")
    private String title;

    @JsonProperty("datePublished") @JsonDeserialize(using = DateDeserializer.class)
    private Date releaseDate;
    @JsonProperty("image")
    private String coverImageUrl;
    @JsonProperty("duration") @JsonDeserialize(using = DurationDeserializer.class)
    private Duration duration;

    @JsonProperty("description")
    private String description;
    @JsonProperty("director")  @JsonDeserialize(contentUsing = CreatorDeserializer.class)
    private List<Person> director;

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id);
    }
}
