package com.example.lab1.Service;

import com.example.lab1.Config.CacheNames;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final RestTemplate restTemplate;

    @Value("${movie-database.api.url}")
    private String omdbApiUrl;

    @Value("${movie-database.api.key}")
    private String omdbApiKey;

    @Cacheable(value = CacheNames.MOVIES, key = "'all'")
    public List<Movie> getAllMovies() {
        log.debug("Fetching all movies from database");
        return movieRepository.findAll();
    }

    @Cacheable(value = CacheNames.MOVIE_INFO, key = "#title.toLowerCase()")
    public String getMovieInfoByTitle(String title) {
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
        log.info("Saving movie: {}", movie.getTitle());
        return movieRepository.save(movie);
    }

    @CacheEvict(value = CacheNames.MOVIES, key = "'all'")
    @Transactional
    public void deleteMovie(Long id) {
        log.info("Deleting movie with ID: {}", id);
        if (!movieRepository.existsById(id)) {
            throw new EntityNotFoundException("Movie not found with id: " + id);
        }
        movieRepository.deleteById(id);
    }
}