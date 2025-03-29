package com.example.lab1.Service;

import com.example.lab1.Entity.Movie;
import com.example.lab1.Repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
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

    public MovieService(MovieRepository movieRepository, RestTemplate restTemplate) {
        this.movieRepository = movieRepository;
        this.restTemplate = restTemplate;
    }

    public String getMovieInfoByTitle(String title) {
        String requestUrl = omdbApiUrl + "?t=" + title + "&apikey=" + omdbApiKey;
        return restTemplate.getForObject(requestUrl, String.class);
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Transactional
    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    @Transactional
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }
}