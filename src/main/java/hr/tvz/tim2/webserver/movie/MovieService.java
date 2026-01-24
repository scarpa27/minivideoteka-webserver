package hr.tvz.tim2.webserver.movie;

import hr.tvz.tim2.webserver.dto.DtoMapper;
import hr.tvz.tim2.webserver.dto.MovieDto;
import hr.tvz.tim2.webserver.movie.domain.MovieFilter;
import hr.tvz.tim2.webserver.movie.domain.MovieSpecs;
import hr.tvz.tim2.webserver.movie.entities.CompanyEntity;
import hr.tvz.tim2.webserver.movie.entities.CreatorEntity;
import hr.tvz.tim2.webserver.movie.entities.MovieEntity;
import hr.tvz.tim2.webserver.movie.entities.PersonEntity;
import hr.tvz.tim2.webserver.movie.repository.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Qualifier("movieService")
@Setter
@Getter
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
        log.debug("MovieService created");
    }

    public List<MovieEntity> getAllMovies() {
        log.debug("Getting all movies");
        return movieDbRepository.findAll().stream().toList();
    }

    public void createOrUpdate(MovieDto dto) {
        log.debug("Saving movie with id: {}", dto.getId());
        var entity = movieDbRepository.findById(dto.getId()).orElseGet(MovieEntity::new);

        entity.setId(dto.getId());

        updateCreators(dto.getCreators());
        updatePeople(dto.getActors());
        updatePeople(dto.getDirectors());

        entity.setCreators(new HashSet<>());
        entity.setActors(new HashSet<>());
        entity.setDirectors(new HashSet<>());

        dto.getCreators().forEach(c -> entity.getCreators().add(creatorDbRepository.getReferenceById(c.getId())));
        dto.getActors().forEach(p -> entity.getActors().add(personDbRepository.getReferenceById(p.getId())));
        dto.getDirectors().forEach(p -> entity.getDirectors().add(personDbRepository.getReferenceById(p.getId())));

        entity.setDescription(dto.getDescription());
        entity.setCoverImageUrl(dto.getCoverImageUrl());
        entity.setDuration(dto.getDuration());
        entity.setReleaseDate(dto.getReleaseDate());
        entity.setTitle(dto.getTitle());

        movieDbRepository.saveAndFlush(entity);
    }

    public List<MovieDto> getSpecificListDto(List<String> ids) {
        log.debug("Getting movies with ids: {}", ids);
        return movieDbRepository.findAllById(ids).stream().map(DtoMapper::toDto).toList();
    }

    private void updateCreators(List<MovieDto.CreatorDto> creators) {
        log.debug("Updating creators: {}", creators);
        creators.forEach(dto -> {
            CreatorEntity ce;
            Optional<CreatorEntity> opt = creatorDbRepository.findById(dto.getId());
            if (opt.isEmpty()) {
                if (dto.getId().startsWith("nm")) {
                    ce = new PersonEntity(dto.getId(), dto.getName());
                }
                else if (dto.getId().startsWith("co")) {
                    ce = new CompanyEntity(dto.getId());
                }
                else {
                    throw new IllegalArgumentException("Unknown creator type! Person id starts with 'nm' and company id starts with 'co'.");
                }
            }
            else {
                ce = opt.get();
            }
            ce.setName(dto.getName());
            creatorDbRepository.saveAndFlush(ce);
        });
    }

    private void updatePeople(List<MovieDto.CreatorDto> people) {
        log.debug("Updating people: {}", people);
        people.forEach(dto -> {
            PersonEntity pe;
            Optional<PersonEntity> opt = personDbRepository.findById(dto.getId());
            if (opt.isEmpty()) {
                if (dto.getId().startsWith("nm")) {
                    pe = new PersonEntity(dto.getId(), dto.getName());
                }
                else {
                    throw new IllegalArgumentException("Unknown person type! Person id starts with 'nm'.");
                }
            }
            else {
                pe = opt.get();
            }
            pe.setName(dto.getName());
            personDbRepository.saveAndFlush(pe);
        });
    }

    private void updateCompanies(List<MovieDto.CreatorDto> companies) {
        log.debug("Updating companies: {}", companies);
        companies.forEach(dto -> {
            CompanyEntity ce;
            Optional<CompanyEntity> opt = companyDbRepository.findById(dto.getId());
            if (opt.isEmpty()) {
                if (dto.getId().startsWith("co")) {
                    ce = new CompanyEntity(dto.getId());
                }
                else {
                    throw new IllegalArgumentException("Unknown company type! Company id starts with 'co'.");
                }
            }
            else {
                ce = opt.get();
            }
            ce.setName(dto.getName());
            companyDbRepository.saveAndFlush(ce);
        });
    }

    public MovieDto getSpecificDto(String id) {
        log.debug("Getting movie with id: {}", id);
        return movieDbRepository.findById(id).map(DtoMapper::toDto).orElse(null);
    }

    public List<PersonEntity> getAllActors() {
        log.debug("Getting all actors");
        var actorsSorted = personDbRepository.findActorsSortByCreated();
        log.info("There are {} sorted actors", actorsSorted.size());
        return actorsSorted;
    }

    public List<PersonEntity> getAllPeople() {
        log.debug("Getting all people");
        return personDbRepository.findAll();
    }

    public List<MovieEntity> getMoviesByActor(String actorId) {
        log.debug("Getting movies by actor with id: {}", actorId);
        return movieDbRepository.findByActors_Id(actorId);
    }

    public List<MovieEntity> getFilteredMovies(String keyword) {
        log.debug("Getting movies by keyword: {}", keyword);
        return movieDbRepository.findByKeyword(keyword).stream().toList();
    }

    public Page<MovieDto> getMovies(MovieFilter filter, Pageable pageable) {
        var spec = MovieSpecs.withFilter(filter);
        return movieDbRepository.findAll(spec, pageable)
                .map(DtoMapper::toDto);
    }

    public void saveAllMovies(Iterable<MovieEntity> movies) {
        log.debug("Saving movies");
        movieDbRepository.saveAllAndFlush(movies);
    }

    public void setUpMovies() {
        log.info("Setting up movies with hard-coded list");
        movieDbRepository.deleteAll();
        creatorDbRepository.deleteAll();
        personDbRepository.deleteAll();
        companyDbRepository.deleteAll();

        try {
            assignFakeMovies();
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void assignFakeMovies() throws IOException {
        log.info("Assigning fake movies");
        List<MovieEntity> allMovies = fakeRepository.getAllMovies();
        Set<CreatorEntity> allCreators = new HashSet<>();
        Set<PersonEntity> allPeople = new HashSet<>();
        Set<CompanyEntity> allCompanies = new HashSet<>();
        allMovies.forEach(movie -> {
            allCreators.addAll(movie.getCreators());
            allPeople.addAll(movie.getActors());
            allPeople.addAll(movie.getDirectors());
            allPeople.addAll(movie.getCreators().stream().filter(PersonEntity.class::isInstance).map(c -> (PersonEntity) c).toList());
            allCompanies.addAll(movie.getCreators().stream().filter(CompanyEntity.class::isInstance).map(c -> (CompanyEntity) c).toList());
        });

        creatorDbRepository.saveAllAndFlush(allCreators);
        personDbRepository.saveAllAndFlush(allPeople);
        companyDbRepository.saveAllAndFlush(allCompanies);

        for (MovieEntity movie : allMovies) {
            if (movie.getYoutubeTrailer() != null)
                movie.getYoutubeTrailer().setMovie(movie);
            else
                log.warn("No trailer found for movie {}-{}", movie.getId(), movie.getTitle());

            Set<CreatorEntity> movieCreators = new HashSet<>();
            Set<PersonEntity> movieActors = new HashSet<>();
            Set<PersonEntity> movieDirectors = new HashSet<>();

            movie.getCreators().forEach(creator -> creatorDbRepository.findById(creator.getId()).ifPresent(movieCreators::add));

            movie.getActors().forEach(person -> personDbRepository.findById(person.getId()).ifPresent(movieActors::add));

            movie.getDirectors().forEach(person -> personDbRepository.findById(person.getId()).ifPresent(movieDirectors::add));

            movie.setCreators(movieCreators);
            movie.setActors(movieActors);
            movie.setDirectors(movieDirectors);
        }

        movieDbRepository.saveAllAndFlush(allMovies);

        log.info("There are {} movies. {} creators, {} of which are person, and {} are companies.%n",
                 movieDbRepository.count(), creatorDbRepository.count(), personDbRepository.count(), companyDbRepository.count());
    }
}
