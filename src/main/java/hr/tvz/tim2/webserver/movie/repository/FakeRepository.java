package hr.tvz.tim2.webserver.movie.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.tvz.tim2.webserver.movie.entities.MovieEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
@Getter
@NoArgsConstructor
public class FakeRepository {
    public List<MovieEntity> getAllMovies() throws IOException {
        var inputStream = FakeRepository.class.getClassLoader().getResourceAsStream("top250.json");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(inputStream, new TypeReference<>() {});
    }
}
