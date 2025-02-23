package com.example.lab1.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class MovieController {

    private final String MovieDataBaseApiUrl;
    private final String MovieDataBaseApiKey;
    private final RestTemplate restTemplate;

    public MovieController(@Value("${movie-database.api.url}") String MovieDataBaseApiUrl,
                           @Value("${movie-database.api.key}") String MovieDataBaseApiKey,
                           RestTemplate restTemplate) {
        this.MovieDataBaseApiUrl = MovieDataBaseApiUrl;
        this.MovieDataBaseApiKey = MovieDataBaseApiKey;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/movie")
    public String getMovieInfoByTitle(@RequestParam String title) {
        String requestUrl = MovieDataBaseApiUrl + "?t=" + title + "&apikey=" + MovieDataBaseApiKey;
        return restTemplate.getForObject(requestUrl, String.class);
    }
}