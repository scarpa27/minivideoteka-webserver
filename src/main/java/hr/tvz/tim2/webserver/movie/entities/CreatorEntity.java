package hr.tvz.tim2.webserver.movie.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSubTypes({@JsonSubTypes.Type(value = CompanyEntity.class), @JsonSubTypes.Type(value = PersonEntity.class)})
public abstract class CreatorEntity {

    @Id
    @JsonProperty("url")
    private String id;

    @JsonProperty("name")
    private String name;

    @ManyToMany(mappedBy = "creators")
    Set<MovieEntity> createdMovies = new HashSet<>();

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CreatorEntity creator = (CreatorEntity) o;
        return Objects.equals(id, creator.id);
    }
}