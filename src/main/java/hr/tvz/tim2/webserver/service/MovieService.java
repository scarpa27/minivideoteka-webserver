package hr.tvz.tim2.webserver.service;

import hr.tvz.tim2.webserver.domain.Company;
import hr.tvz.tim2.webserver.domain.Creator;
import hr.tvz.tim2.webserver.domain.Movie;
import hr.tvz.tim2.webserver.domain.Person;
import hr.tvz.tim2.webserver.dto.DtoMapper;
import hr.tvz.tim2.webserver.dto.MovieDto;
import hr.tvz.tim2.webserver.persistance.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Qualifier("movieService")
@Setter @Getter
public class MovieService {
    private final Repository repository;

    private final MovieDbRepo movieDbRepo;
    private final CreatorDbRepository creatorDbRepository;
    private final PersonDbRepository personDbRepository;
    private final CompanyDbRepository companyDbRepository;

    @Autowired
    public MovieService(@Qualifier("MRepository") Repository repository,
                        @Autowired MovieDbRepo movieDbRepo,
                        @Autowired CreatorDbRepository creatorDbRepository,
                        @Autowired PersonDbRepository personDbRepository,
                        @Autowired CompanyDbRepository companyDbRepository) {
        this.repository = repository;
        this.movieDbRepo = movieDbRepo;

        this.creatorDbRepository = creatorDbRepository;
        this.personDbRepository = personDbRepository;
        this.companyDbRepository = companyDbRepository;

//        assignFakeMovies();
    }

    public List<Movie> getAllMovies() {
        return movieDbRepo.findAll().stream().toList();
    }

    public List<MovieDto> getSpecificListDto(List<String> ids) {
        return movieDbRepo.findAllById(ids).stream().map(DtoMapper::toDto).toList();
    }

    public MovieDto getSpecificDto(String id) {
        return movieDbRepo.findById(id).map(DtoMapper::toDto).orElse(null);
    }

    public List<Person> getAllActors() {
        var actorsSorted = personDbRepository.findActorsSortByCreated();
        System.out.printf("There are %d sorted actors.\n", actorsSorted.size());
        return actorsSorted;
//        return repository.getAllActors().stream().toList();
    }

    public List<Person> getAllPeople() {
        return personDbRepository.findAll();
    }

    public List<Movie> getMoviesByActor(String actorId) {
        return repository.getAllMoviesByActor(actorId);
    }

    public List<Movie> getFilteredMovies(String keyword) {
        return movieDbRepo.findByKeyword(keyword).stream().toList();
    }

    public void saveAllMovies(Iterable<Movie> movies) {
        movieDbRepo.saveAllAndFlush(movies);
    }

    public void setUpMovies() {
        movieDbRepo.deleteAll();
        creatorDbRepository.deleteAll();
        personDbRepository.deleteAll();
        companyDbRepository.deleteAll();

        assignFakeMovies();
    }

    private void assignFakeMovies() {
            List<Movie> allMovies = repository.getAllMovies();
            Set<Creator> allCreators = new HashSet<>();
            Set<Person> allPeople = new HashSet<>();
            Set<Company> allCompanies = new HashSet<>();
            allMovies.forEach(movie -> {
                allCreators.addAll(movie.getCreators());
                allPeople.addAll(movie.getActors());
                allPeople.addAll(movie.getDirectors());
                allPeople.addAll(movie.getCreators().stream().filter(c -> c instanceof Person).map(c -> (Person)c).toList());
                allCompanies.addAll(movie.getCreators().stream().filter(c -> c instanceof Company).map(c -> (Company)c).toList());
            });

            creatorDbRepository.saveAllAndFlush(allCreators);
            personDbRepository.saveAllAndFlush(allPeople);
            companyDbRepository.saveAllAndFlush(allCompanies);

            for (Movie movie : allMovies) {
                Set<Creator> movieCreators = new HashSet<>();
                Set<Person> movieActors = new HashSet<>();
                Set<Person> movieDirectors = new HashSet<>();

                movie.getCreators().forEach(creator -> {
                    creatorDbRepository.findById(creator.getId()).ifPresent(movieCreators::add);
                });

                movie.getActors().forEach(person -> {
                    personDbRepository.findById(person.getId()).ifPresent(movieActors::add);
                });

                movie.getDirectors().forEach(person -> {
                    personDbRepository.findById(person.getId()).ifPresent(movieDirectors::add);
                });

                movie.setCreators(movieCreators);
                movie.setActors(movieActors);
                movie.setDirectors(movieDirectors);
            }

            movieDbRepo.saveAllAndFlush(allMovies);

            System.out.printf("There are %d movies. %d creators, %d of which are person, and %d are companies.%n",
                              movieDbRepo.count(), creatorDbRepository.count(), personDbRepository.count(), companyDbRepository.count());
    }
}
