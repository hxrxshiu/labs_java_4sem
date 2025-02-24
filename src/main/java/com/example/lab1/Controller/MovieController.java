package com.example.lab1.Controller;

import com.example.lab1.Service.MovieService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/movie")
    public String getMovieInfoByTitle(@RequestParam String title) {
        return movieService.getMovieInfoByTitle(title);
    }
}