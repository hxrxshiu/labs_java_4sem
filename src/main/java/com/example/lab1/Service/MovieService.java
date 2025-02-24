package com.example.lab1.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MovieService {

    private final String movieDataBaseApiUrl;
    private final String movieDataBaseApiKey;
    private final RestTemplate restTemplate;

    public MovieService(@Value("${movie-database.api.url}") String movieDataBaseApiUrl,
                        @Value("${movie-database.api.key}") String movieDataBaseApiKey,
                        RestTemplate restTemplate) {
        this.movieDataBaseApiUrl = movieDataBaseApiUrl;
        this.movieDataBaseApiKey = movieDataBaseApiKey;
        this.restTemplate = restTemplate;
    }

    public String getMovieInfoByTitle(String title) {
        String requestUrl = movieDataBaseApiUrl + "?t=" + title + "&apikey=" + movieDataBaseApiKey;
        return restTemplate.getForObject(requestUrl, String.class);
    }
}