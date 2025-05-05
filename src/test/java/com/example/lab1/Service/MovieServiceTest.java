package com.example.lab1.Service;

import com.example.lab1.Entity.Actor;
import com.example.lab1.Entity.Movie;
import com.example.lab1.Exception.EntityNotFoundException;
import com.example.lab1.Exception.ExternalServiceException;
import com.example.lab1.Repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MovieService movieService;

    @Test
    void shouldReturnAllMovies() {
        Movie movie = mock(Movie.class);
        when(movie.getTitle()).thenReturn("Test Movie");
        when(movieRepository.findAll()).thenReturn(Collections.singletonList(movie));

        List<Movie> result = movieService.getAllMovies();

        assertEquals(1, result.size());
        assertEquals("Test Movie", result.get(0).getTitle());
    }

    @Test
    void shouldReturnMovieInfoByTitle() {
        String response = "{\"Title\":\"Inception\",\"Year\":\"2010\"}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(response);

        String result = movieService.getMovieInfoByTitle("Inception");

        assertNotNull(result);
        assertTrue(result.contains("Inception"));
    }

    @Test
    void shouldThrowExceptionWhenApiReturnsError() {
        String response = "{\"Error\":\"Movie not found\"}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(response);

        assertThrows(ExternalServiceException.class, () -> movieService.getMovieInfoByTitle("Unknown"));
    }

    @Test
    void shouldSaveMovie() {
        Movie movie = mock(Movie.class);
        when(movie.getTitle()).thenReturn("Test Movie");

        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        Movie result = movieService.saveMovie(movie);

        assertEquals("Test Movie", result.getTitle());
        verify(movieRepository).save(movie);
    }

    @Test
    void shouldDeleteMovieWhenExists() {
        when(movieRepository.existsById(1L)).thenReturn(true);

        movieService.deleteMovie(1L);

        verify(movieRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenMovieNotFoundDuringDelete() {
        when(movieRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> movieService.deleteMovie(1L));
    }

    @Test
    void shouldSaveMoviesInBulkWhenAllValid() {
        Movie movie1 = mock(Movie.class);
        when(movie1.getTitle()).thenReturn("Movie 1");

        Movie movie2 = mock(Movie.class);
        when(movie2.getTitle()).thenReturn("Movie 2");

        List<Movie> movies = Arrays.asList(movie1, movie2);

        when(movieRepository.save(any(Movie.class))).thenAnswer(i -> i.getArgument(0));

        List<Movie> result = movieService.saveMoviesBulk(movies);

        assertEquals(2, result.size());
        verify(movieRepository, times(2)).save(any(Movie.class));
    }

    @Test
    void shouldFilterInvalidMoviesWhenSavingInBulk() {
        Movie invalidMovie = mock(Movie.class);
        when(invalidMovie.getTitle()).thenReturn("");

        Movie validMovie = mock(Movie.class);
        when(validMovie.getTitle()).thenReturn("Valid Movie");

        List<Movie> movies = Arrays.asList(invalidMovie, validMovie, null);

        when(movieRepository.save(any(Movie.class))).thenAnswer(i -> i.getArgument(0));

        List<Movie> result = movieService.saveMoviesBulk(movies);

        assertEquals(1, result.size());
        assertEquals("Valid Movie", result.get(0).getTitle());
    }

    @Test
    void shouldSaveMoviesWithActorsInBulk() {
        Movie movie = mock(Movie.class);
        when(movie.getTitle()).thenReturn("Movie with Actors");

        Actor actor1 = mock(Actor.class);
        when(actor1.getName()).thenReturn("Actor 1");

        Actor actor2 = mock(Actor.class);
        when(actor2.getName()).thenReturn("Actor 2");

        movie.setActors(Arrays.asList(actor1, actor2));

        when(movieRepository.save(any(Movie.class))).thenAnswer(i -> {
            Movie m = i.getArgument(0);
            m.getActors().forEach(a -> a.setId(1L));
            return m;
        });

        List<Movie> result = movieService.saveMoviesBulk(Collections.singletonList(movie));

        assertEquals(2, result.get(0).getActors().size());
        assertEquals("Actor 1", result.get(0).getActors().get(0).getName());
    }
}
