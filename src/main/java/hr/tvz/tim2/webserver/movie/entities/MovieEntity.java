package hr.tvz.tim2.webserver.movie.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import hr.tvz.tim2.webserver.common.deserializer.CreatorDeserializer;
import hr.tvz.tim2.webserver.common.deserializer.DateDeserializer;
import hr.tvz.tim2.webserver.common.deserializer.DurationDeserializer;
import hr.tvz.tim2.webserver.common.deserializer.MovieIdDeserializer;
import hr.tvz.tim2.webserver.review.ReviewEntity;
import hr.tvz.tim2.webserver.stock.StockEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class MovieEntity {
    @Id
    @JsonProperty("url") @JsonDeserialize(using = MovieIdDeserializer.class)
    private String id;

    @JsonProperty("youtubeTrailer")
    @OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    @JoinColumn(name = "youtube_trailer_video_id", referencedColumnName = "videoId")
    private YoutubeTrailerEntity youtubeTrailer;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "movie_actor_join",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    @JsonProperty("actor") @JsonDeserialize(contentUsing = CreatorDeserializer.class)
    private Set<PersonEntity> actors = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "movie_creator_join",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "creator_id")
    )
    @JsonProperty("creator")  @JsonDeserialize(contentUsing = CreatorDeserializer.class)
    private Set<CreatorEntity> creators;

    @JsonProperty("name")
    private String title;

    @JsonProperty("datePublished") @JsonDeserialize(using = DateDeserializer.class)
    private LocalDate releaseDate;

    @JsonProperty("image")
    private String coverImageUrl;

    @JsonProperty("duration") @JsonDeserialize(using = DurationDeserializer.class)
    private Duration duration;

    @JsonProperty("description")
    private String description;

    private BigDecimal rating;

    @JsonProperty("aggregateRating")
    public void setRating(JsonNode agg) {
        this.rating = (agg != null && agg.hasNonNull("ratingValue"))
                ? agg.get("ratingValue").decimalValue()
                : null;
    }

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "movie_director_join",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    @JsonProperty("director")  @JsonDeserialize(contentUsing = CreatorDeserializer.class)
    private Set<PersonEntity> directors;

    @OneToOne(mappedBy = "movie", cascade = CascadeType.ALL)
    private StockEntity stock;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ReviewEntity> reviews = new HashSet<>();

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MovieEntity movie = (MovieEntity) o;
        return Objects.equals(id, movie.id);
    }
}