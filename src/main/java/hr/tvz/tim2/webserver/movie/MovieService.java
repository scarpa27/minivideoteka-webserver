package hr.tvz.tim2.webserver.movie;

import hr.tvz.tim2.webserver.movie.entities.CompanyEntity;
import hr.tvz.tim2.webserver.movie.entities.CreatorEntity;
import hr.tvz.tim2.webserver.movie.entities.MovieEntity;
import hr.tvz.tim2.webserver.movie.entities.PersonEntity;
import hr.tvz.tim2.webserver.dto.DtoMapper;
import hr.tvz.tim2.webserver.dto.MovieDto;
import hr.tvz.tim2.webserver.movie.repository.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Qualifier("movieService")
@Setter @Getter
public class MovieService {
    private final FakeRepository fakeRepository;

    private final MovieDbRepository movieDbRepository;
    private final CreatorDbRepository creatorDbRepository;
    private final PersonDbRepository personDbRepository;
    private final CompanyDbRepository companyDbRepository;

    @Autowired
    public MovieService(@Autowired FakeRepository fakeRepository,
                        @Autowired MovieDbRepository movieDbRepository,
                        @Autowired CreatorDbRepository creatorDbRepository,
                        @Autowired PersonDbRepository personDbRepository,
                        @Autowired CompanyDbRepository companyDbRepository) {
        this.fakeRepository = fakeRepository;
        this.movieDbRepository = movieDbRepository;

        this.creatorDbRepository = creatorDbRepository;
        this.personDbRepository = personDbRepository;
        this.companyDbRepository = companyDbRepository;
    }

    public List<MovieEntity> getAllMovies() {
        return movieDbRepository.findAll().stream().toList();
    }

    public List<MovieDto> getSpecificListDto(List<String> ids) {
        return movieDbRepository.findAllById(ids).stream().map(DtoMapper::toDto).toList();
    }

    public MovieDto getSpecificDto(String id) {
        return movieDbRepository.findById(id).map(DtoMapper::toDto).orElse(null);
    }

    public List<PersonEntity> getAllActors() {
        var actorsSorted = personDbRepository.findActorsSortByCreated();
        System.out.printf("There are %d sorted actors.\n", actorsSorted.size());
        return actorsSorted;
    }

    public List<PersonEntity> getAllPeople() {
        return personDbRepository.findAll();
    }

    public List<MovieEntity> getMoviesByActor(String actorId) {
        return movieDbRepository.findByActors_Id(actorId);
    }

    public List<MovieEntity> getFilteredMovies(String keyword) {
        return movieDbRepository.findByKeyword(keyword).stream().toList();
    }

    public void saveAllMovies(Iterable<MovieEntity> movies) {
        movieDbRepository.saveAllAndFlush(movies);
    }

    public void setUpMovies() throws IOException {
        movieDbRepository.deleteAll();
        creatorDbRepository.deleteAll();
        personDbRepository.deleteAll();
        companyDbRepository.deleteAll();

        assignFakeMovies();
    }

    private void assignFakeMovies() throws IOException {
            List<MovieEntity> allMovies = fakeRepository.getAllMovies();
            Set<CreatorEntity> allCreators = new HashSet<>();
            Set<PersonEntity> allPeople = new HashSet<>();
            Set<CompanyEntity> allCompanies = new HashSet<>();
            allMovies.forEach(movie -> {
                allCreators.addAll(movie.getCreators());
                allPeople.addAll(movie.getActors());
                allPeople.addAll(movie.getDirectors());
                allPeople.addAll(movie.getCreators().stream().filter(PersonEntity.class::isInstance).map(c -> (PersonEntity)c).toList());
                allCompanies.addAll(movie.getCreators().stream().filter(CompanyEntity.class::isInstance).map(c -> (CompanyEntity)c).toList());
            });

            creatorDbRepository.saveAllAndFlush(allCreators);
            personDbRepository.saveAllAndFlush(allPeople);
            companyDbRepository.saveAllAndFlush(allCompanies);

            for (MovieEntity movie : allMovies) {
                Set<CreatorEntity> movieCreators = new HashSet<>();
                Set<PersonEntity> movieActors = new HashSet<>();
                Set<PersonEntity> movieDirectors = new HashSet<>();

                movie.getCreators().forEach(creator ->
                      creatorDbRepository.findById(creator.getId()).ifPresent(movieCreators::add));

                movie.getActors().forEach(person ->
                      personDbRepository.findById(person.getId()).ifPresent(movieActors::add));

                movie.getDirectors().forEach(person ->
                      personDbRepository.findById(person.getId()).ifPresent(movieDirectors::add));

                movie.setCreators(movieCreators);
                movie.setActors(movieActors);
                movie.setDirectors(movieDirectors);
            }

            movieDbRepository.saveAllAndFlush(allMovies);

            System.out.printf("There are %d movies. %d creators, %d of which are person, and %d are companies.%n",
                              movieDbRepository.count(), creatorDbRepository.count(), personDbRepository.count(), companyDbRepository.count());
    }
}
