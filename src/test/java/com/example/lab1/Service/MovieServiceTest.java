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
        Movie movie = new Movie();
        movie.setTitle("Test Movie");

        when(movieRepository.save(any())).thenReturn(movie);

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
        List<Movie> movies = Arrays.asList(
                createTestMovie("Movie 1"),
                createTestMovie("Movie 2")
        );

        when(movieRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        List<Movie> result = movieService.saveMoviesBulk(movies);

        assertEquals(2, result.size());
        verify(movieRepository, times(2)).save(any());
    }

    @Test
    void shouldFilterInvalidMoviesWhenSavingInBulk() {
        List<Movie> movies = Arrays.asList(
                createTestMovie(""),
                createTestMovie("Valid Movie"),
                createTestMovie(null)
        );

        when(movieRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        List<Movie> result = movieService.saveMoviesBulk(movies);

        assertEquals(1, result.size());
        assertEquals("Valid Movie", result.get(0).getTitle());
    }

    @Test
    void shouldSaveMoviesWithActorsInBulk() {
        Movie movie = createTestMovie("Movie with Actors");
        Actor actor1 = new Actor();
        actor1.setName("Actor 1");
        Actor actor2 = new Actor();
        actor2.setName("Actor 2");
        movie.setActors(Arrays.asList(actor1, actor2));

        when(movieRepository.save(any())).thenAnswer(i -> {
            Movie m = i.getArgument(0);
            m.getActors().forEach(a -> a.setId(1L));
            return m;
        });

        List<Movie> result = movieService.saveMoviesBulk(Collections.singletonList(movie));

        assertEquals(2, result.get(0).getActors().size());
        assertEquals("Actor 1", result.get(0).getActors().get(0).getName());
    }

    private Movie createTestMovie(String title) {
        Movie movie = new Movie();
        movie.setTitle(title);
        return movie;
    }
}
