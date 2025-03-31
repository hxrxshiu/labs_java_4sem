package com.example.lab1.Service;

import com.example.lab1.Entity.Movie;
import com.example.lab1.Repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final RestTemplate restTemplate;
    private final ConcurrentHashMap<String, List<Movie>> moviesCache;
    private final ConcurrentHashMap<String, String> movieInfoCache;

    @Value("${movie-database.api.url}")
    private String omdbApiUrl;

    @Value("${movie-database.api.key}")
    private String omdbApiKey;

    @Autowired
    public MovieService(MovieRepository movieRepository,
                        RestTemplate restTemplate) {
        this.movieRepository = movieRepository;
        this.restTemplate = restTemplate;
        this.moviesCache = new ConcurrentHashMap<>();
        this.movieInfoCache = new ConcurrentHashMap<>();
    }

    public List<Movie> getAllMovies() {
        String cacheKey = "all_movies";

        List<Movie> cachedMovies = moviesCache.get(cacheKey);
        if (cachedMovies != null) {
            return cachedMovies;
        }

        List<Movie> movies = movieRepository.findAll();
        moviesCache.put(cacheKey, movies);
        return movies;
    }

    public String getMovieInfoByTitle(String title) {
        String cacheKey = "movie_info_" + title.toLowerCase();

        String cachedInfo = movieInfoCache.get(cacheKey);
        if (cachedInfo != null) {
            return cachedInfo;
        }

        String requestUrl = omdbApiUrl + "?t=" + title + "&apikey=" + omdbApiKey;
        String result = restTemplate.getForObject(requestUrl, String.class);

        if (result == null) {
            throw new RuntimeException("Failed to fetch movie info for title: " + title);
        }

        movieInfoCache.put(cacheKey, result);
        return result;
    }

    @Transactional
    public Movie saveMovie(Movie movie) {
        Movie savedMovie = movieRepository.save(movie);
        moviesCache.remove("all_movies");
        return savedMovie;
    }

    @Transactional
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
        moviesCache.remove("all_movies");
    }
}