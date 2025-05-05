package com.example.lab1.Service;

import com.example.lab1.Config.CacheNames;
import com.example.lab1.Entity.Actor;
import com.example.lab1.Entity.Movie;
import com.example.lab1.Exception.EntityNotFoundException;
import com.example.lab1.Exception.ExternalServiceException;
import com.example.lab1.Repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final RestTemplate restTemplate;
    private final RequestCounter requestCounter;

    @Value("${movie-database.api.url}")
    private String omdbApiUrl;

    @Value("${movie-database.api.key}")
    private String omdbApiKey;

    @Cacheable(value = CacheNames.MOVIES, key = "'all'")
    public List<Movie> getAllMoviesCached() {
        requestCounter.increment();
        log.debug("Fetching all movies from database");
        return movieRepository.findAll();
    }

    public List<Movie> getAllMovies() {
        return getAllMoviesCached();
    }

    public String getMovieInfoByTitle(String title) {
        requestCounter.increment();
        log.info("Fetching movie info for title: {}", title);
        String requestUrl = String.format("%s?t=%s&apikey=%s", omdbApiUrl, title, omdbApiKey);

        try {
            String result = restTemplate.getForObject(requestUrl, String.class);
            if (result == null || result.contains("\"Error\":")) {
                throw new ExternalServiceException("Failed to fetch movie info for title: " + title);
            }
            return result;
        } catch (Exception e) {
            throw new ExternalServiceException("Error while calling OMDb API: " + e.getMessage());
        }
    }

    @CacheEvict(value = CacheNames.MOVIES, key = "'all'")
    @Transactional
    public Movie saveMovie(Movie movie) {
        requestCounter.increment();
        log.info("Saving movie: {}", movie.getTitle());
        return movieRepository.save(movie);
    }

    @CacheEvict(value = CacheNames.MOVIES, key = "'all'")
    @Transactional
    public void deleteMovie(Long id) {
        requestCounter.increment();
        log.info("Deleting movie with ID: {}", id);
        if (!movieRepository.existsById(id)) {
            throw new EntityNotFoundException("Movie not found with id: " + id);
        }
        movieRepository.deleteById(id);
    }

    @CacheEvict(value = CacheNames.MOVIES, key = "'all'")
    @Transactional
    public List<Movie> saveMoviesBulk(List<Movie> movies) {
        requestCounter.increment();
        log.info("Saving {} movies in bulk", movies.size());

        return movies.stream()
                .filter(movie -> movie.getTitle() != null && !movie.getTitle().trim().isEmpty())
                .map(movie -> {
                    Movie newMovie = new Movie();
                    newMovie.setTitle(movie.getTitle());

                    if (movie.getActors() != null && !movie.getActors().isEmpty()) {
                        newMovie.setActors(movie.getActors().stream()
                                .map(actor -> {
                                    Actor newActor = new Actor();
                                    newActor.setName(actor.getName());
                                    newActor.setMovie(newMovie);
                                    return newActor;
                                })
                                .collect(Collectors.toList()));
                    }

                    return movieRepository.save(newMovie);
                })
                .collect(Collectors.toList());
    }
}


