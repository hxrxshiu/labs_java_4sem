package com.example.lab1.Service;

import com.example.lab1.Config.CacheNames;
import com.example.lab1.Entity.Movie;
import com.example.lab1.Repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final RestTemplate restTemplate;

    @Value("${movie-database.api.url}")
    private String omdbApiUrl;

    @Value("${movie-database.api.key}")
    private String omdbApiKey;

    @Autowired
    public MovieService(MovieRepository movieRepository, RestTemplate restTemplate) {
        this.movieRepository = movieRepository;
        this.restTemplate = restTemplate;
    }

    @Cacheable(value = CacheNames.MOVIES, key = "'all'")
    public List<Movie> getAllMovies() {
        System.out.println("Loading movies from DB...");
        return movieRepository.findAll();
    }

    @Cacheable(value = CacheNames.MOVIE_INFO, key = "#title.toLowerCase()")
    public String getMovieInfoByTitle(String title) {
        System.out.println("Fetching movie info from API for: " + title);
        String requestUrl = omdbApiUrl + "?t=" + title + "&apikey=" + omdbApiKey;
        String result = restTemplate.getForObject(requestUrl, String.class);
        if (result == null) {
            throw new RuntimeException("Failed to fetch movie info for title: " + title);
        }
        return result;
    }

    @CacheEvict(value = CacheNames.MOVIES, key = "'all'")
    @Transactional
    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    @CacheEvict(value = CacheNames.MOVIES, key = "'all'")
    @Transactional
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }
}