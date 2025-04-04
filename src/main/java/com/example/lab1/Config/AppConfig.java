package com.example.lab1.Config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.DefaultResponseErrorHandler;

@Configuration
@EnableCaching
public class AppConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(CacheNames.MOVIES, CacheNames.ACTORS, CacheNames.MOVIE_INFO);
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
        return restTemplate;
    }
}