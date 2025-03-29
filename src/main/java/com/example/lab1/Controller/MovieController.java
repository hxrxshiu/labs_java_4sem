package com.example.lab1.Controller;

import com.example.lab1.Entity.Movie;
import com.example.lab1.Service.MovieService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieService movieService;

    // Запрос к внешнему API (OMDb)
    @GetMapping("/search")
    public String searchMovie(@RequestParam String title) {
        return movieService.getMovieInfoByTitle(title);
    }

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

    @PostMapping
    public Movie createMovie(@RequestBody Movie movie) {
        return movieService.saveMovie(movie);
    }

    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
    }
}