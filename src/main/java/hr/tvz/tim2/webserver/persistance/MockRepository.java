package hr.tvz.tim2.webserver.persistance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.tvz.tim2.webserver.domain.Movie;
import hr.tvz.tim2.webserver.domain.Person;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@org.springframework.stereotype.Repository
@Qualifier("mockRepository")
public class MockRepository implements Repository {
    private final List<Movie> allMovies;
    private final Set<Person> allActors;

    public MockRepository() throws IOException {
        var inputStream = MockRepository.class.getClassLoader().getResourceAsStream("top250.json");
        ObjectMapper mapper = new ObjectMapper();

        allMovies = mapper.readValue(inputStream, new TypeReference<>() {
        });

        allActors = new HashSet<>();
        allMovies.forEach(m -> allActors.addAll(m.getActors()));
    }

    @Override
    public List<Movie> getAllMovies() {
        return allMovies;
    }

    @Override
    public Set<Person> getAllActors() {
        return allActors;
    }

    @Override
    public List<Movie> getAllMoviesByActor(String actorId) {
        var actor = allActors.stream().filter(a -> a.getId().equals(actorId)).findFirst();
        return actor.map(person -> allMovies.stream()
                .filter(m -> m.getActors().contains(person))
                .toList()).orElseGet(List::of);
    }
}
