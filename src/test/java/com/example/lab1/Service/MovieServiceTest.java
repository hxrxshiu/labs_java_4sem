package com.example.lab1.Service;

import com.example.lab1.Entity.Actor;
import com.example.lab1.Entity.Movie;
import com.example.lab1.Exception.EntityNotFoundException;
import com.example.lab1.Exception.ExternalServiceException;
import com.example.lab1.Repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MovieService movieService;

    private Movie testMovie;

    @BeforeEach
    void setUp() {
        testMovie = new Movie();
        testMovie.setId(1L);
        testMovie.setTitle("Test Movie");
    }

    @Test
    void getAllMovies_ShouldReturnAllMovies() {

        when(movieRepository.findAll()).thenReturn(Collections.singletonList(testMovie));

        List<Movie> result = movieService.getAllMovies();

        assertEquals(1, result.size());
        assertEquals("Test Movie", result.get(0).getTitle());
        verify(movieRepository).findAll();
    }

    @Test
    void getMovieInfoByTitle_ShouldReturnMovieInfo() {

        String apiResponse = "{\"Title\":\"Inception\",\"Year\":\"2010\"}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(apiResponse);

        String result = movieService.getMovieInfoByTitle("Inception");

        assertNotNull(result);
        assertTrue(result.contains("Inception"));
        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }

    @Test
    void getMovieInfoByTitle_ShouldThrowExceptionWhenApiFails() {

        String apiResponse = "{\"Error\":\"Movie not found\"}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(apiResponse);


        assertThrows(ExternalServiceException.class, () -> {
            movieService.getMovieInfoByTitle("Unknown Movie");
        });
    }

    @Test
    void saveMovie_ShouldSaveMovie() {

        when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);

        Movie result = movieService.saveMovie(testMovie);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void deleteMovie_ShouldDeleteExistingMovie() {

        when(movieRepository.existsById(1L)).thenReturn(true);
        doNothing().when(movieRepository).deleteById(1L);

        movieService.deleteMovie(1L);

        verify(movieRepository).existsById(1L);
        verify(movieRepository).deleteById(1L);
    }

    @Test
    void deleteMovie_ShouldThrowExceptionWhenMovieNotFound() {

        when(movieRepository.existsById(1L)).thenReturn(false);


        assertThrows(EntityNotFoundException.class, () -> {
            movieService.deleteMovie(1L);
        });
        verify(movieRepository, never()).deleteById(any());
    }

    @Test
    void saveMoviesBulk_ShouldSaveAllValidMovies() {

        List<Movie> movies = Arrays.asList(
                createTestMovie("Movie 1"),
                createTestMovie("Movie 2"),
                createTestMovie("Movie 3")
        );

        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Movie> result = movieService.saveMoviesBulk(movies);

        assertEquals(3, result.size());
        verify(movieRepository, times(3)).save(any(Movie.class));
    }

    @Test
    void saveMoviesBulk_ShouldFilterInvalidMovies() {

        List<Movie> movies = Arrays.asList(
                createTestMovie(""), // invalid
                createTestMovie("Movie 2"),
                createTestMovie(null) // invalid
        );

        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Movie> result = movieService.saveMoviesBulk(movies);

        assertEquals(1, result.size());
        assertEquals("Movie 2", result.get(0).getTitle());
    }

    @Test
    void saveMoviesBulk_ShouldHandleMoviesWithActors() {

        Movie movie = createTestMovie("Movie with Actors");
        Actor actor1 = new Actor();
        actor1.setName("Actor 1");
        Actor actor2 = new Actor();
        actor2.setName("Actor 2");
        movie.setActors(Arrays.asList(actor1, actor2));

        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> {
            Movie saved = invocation.getArgument(0);
            if (saved.getActors() != null) {
                saved.getActors().forEach(a -> a.setId(1L));
            }
            return saved;
        });

        List<Movie> result = movieService.saveMoviesBulk(Collections.singletonList(movie));

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getActors().size());
        assertEquals("Actor 1", result.get(0).getActors().get(0).getName());
    }

    private Movie createTestMovie(String title) {
        Movie movie = new Movie();
        movie.setTitle(title);
        return movie;
    }
}