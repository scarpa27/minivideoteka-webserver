package hr.tvz.tim2.webserver.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Person extends Creator {
    public Person(String id, String name) {
        super.setId(id);
        super.setName(name);
    }

    @ManyToMany(mappedBy = "actors")
    Set<Movie> starredMovies = new HashSet<>();

    @ManyToMany(mappedBy = "directors")
    Set<Movie> directedMovies = new HashSet<>();
}