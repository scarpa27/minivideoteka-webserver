package hr.tvz.tim2.webserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import hr.tvz.tim2.webserver.review.ReviewEntity;
import hr.tvz.tim2.webserver.stock.logic.StockEntity;
import hr.tvz.tim2.webserver.util.deserializer.CreatorDeserializer;
import hr.tvz.tim2.webserver.util.deserializer.DateDeserializer;
import hr.tvz.tim2.webserver.util.deserializer.DurationDeserializer;
import hr.tvz.tim2.webserver.util.deserializer.MovieIdDeserializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Movie {
    @Id
    @JsonProperty("url") @JsonDeserialize(using = MovieIdDeserializer.class)
    private String id;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "movie_actor_join",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    @JsonProperty("actor") @JsonDeserialize(contentUsing = CreatorDeserializer.class)
    private Set<Person> actors = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "movie_creator_join",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "creator_id")
    )
    @JsonProperty("creator")  @JsonDeserialize(contentUsing = CreatorDeserializer.class)
    private Set<Creator> creators;

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

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "movie_director_join",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    @JsonProperty("director")  @JsonDeserialize(contentUsing = CreatorDeserializer.class)
    private Set<Person> directors;

    @OneToOne(mappedBy = "movie", cascade = CascadeType.ALL)
    private StockEntity stock;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReviewEntity> reviews = new HashSet<>();

    /**
     * @param keywords a string that represents a filter keyword
     * @return an int that represents filter rank. Filter matched in specific property ranks higher or lower.
     * title -> 1; description -> 2; creators -> 3; no match -> 0
     * */
    public int condonesToFilterRank(String keywords) {
        if (title.toLowerCase().contains(keywords.toLowerCase())) return 1;

        if (description.toLowerCase().contains(keywords.toLowerCase())) return 2;

        if (String.join(" ", getAllCreatorsNames()).toLowerCase().contains(keywords)) return 3;

        else return 0;
    }

    private List<String> getAllCreatorsNames() {
        List<Creator> crts = new ArrayList<>();
        if (actors != null)
            crts.addAll(actors);
        if (creators != null)
            crts.addAll(creators);
        if (directors != null)
            crts.addAll(directors);
        return crts.stream().map(Creator::getName).collect(Collectors.toList());
    }

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