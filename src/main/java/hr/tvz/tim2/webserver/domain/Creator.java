package hr.tvz.tim2.webserver.domain;

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
@JsonSubTypes({@JsonSubTypes.Type(value = Company.class), @JsonSubTypes.Type(value = Person.class)})
public abstract class Creator {

    @Id
    @JsonProperty("url")
    private String id;

    @JsonProperty("name")
    private String name;

    @ManyToMany(mappedBy = "creators")
    Set<Movie> createdMovies = new HashSet<>();

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Creator creator = (Creator) o;
        return Objects.equals(id, creator.id);
    }
}