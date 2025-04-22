package hr.tvz.tim2.webserver.movie.entities;

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
public class PersonEntity extends CreatorEntity {
    public PersonEntity(String id, String name) {
        super.setId(id);
        super.setName(name);
    }

    @ManyToMany(mappedBy = "actors")
    Set<MovieEntity> starredMovies = new HashSet<>();

    @ManyToMany(mappedBy = "directors")
    Set<MovieEntity> directedMovies = new HashSet<>();
}