package com.example.lab1.Service;

import com.example.lab1.Entity.Actor;
import com.example.lab1.Entity.Movie;
import com.example.lab1.Exception.EntityNotFoundException;
import com.example.lab1.Repository.ActorRepository;
import com.example.lab1.Repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActorServiceTest {

    @Mock
    private ActorRepository actorRepository;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private ActorService actorService;

    private Actor testActor;
    private Movie testMovie;

    @BeforeEach
    void setUp() {
        testMovie = new Movie();
        testMovie.setId(1L);
        testMovie.setTitle("Test Movie");

        testActor = new Actor();
        testActor.setId(1L);
        testActor.setName("Test Actor");
        testActor.setMovie(testMovie);
    }

    @Test
    void getAllActors_ShouldReturnAllActors() {

        when(actorRepository.findAll()).thenReturn(Collections.singletonList(testActor));

        List<Actor> result = actorService.getAllActors();

        assertEquals(1, result.size());
        assertEquals("Test Actor", result.get(0).getName());
        verify(actorRepository).findAll();
    }

    @Test
    void getActorsByMovieId_ShouldReturnActorsForMovie() {

        when(actorRepository.findByMovieId(1L)).thenReturn(Collections.singletonList(testActor));

        List<Actor> result = actorService.getActorsByMovieId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getMovie().getId());
        verify(actorRepository).findByMovieId(1L);
    }

    @Test
    void findActorsByNameContaining_ShouldReturnMatchingActors() {

        when(actorRepository.findByNameContainingIgnoreCase("test")).thenReturn(Collections.singletonList(testActor));

        List<Actor> result = actorService.findActorsByNameContaining("test");

        assertEquals(1, result.size());
        assertEquals("Test Actor", result.get(0).getName());
        verify(actorRepository).findByNameContainingIgnoreCase("test");
    }

    @Test
    void findActorsByNameContaining_ShouldThrowExceptionForEmptyName() {

        assertThrows(IllegalArgumentException.class, () -> {
            actorService.findActorsByNameContaining("");
        });
    }

    @Test
    void saveActor_ShouldSaveActorWithValidMovie() {

        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(actorRepository.save(any(Actor.class))).thenReturn(testActor);

        Actor result = actorService.saveActor(testActor, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getMovie().getId());
        verify(movieRepository).findById(1L);
        verify(actorRepository).save(any(Actor.class));
    }

    @Test
    void saveActor_ShouldThrowExceptionForInvalidMovie() {

        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            actorService.saveActor(testActor, 99L);
        });
    }

    @Test
    void deleteActor_ShouldDeleteExistingActor() {

        when(actorRepository.existsById(1L)).thenReturn(true);
        doNothing().when(actorRepository).deleteById(1L);

        actorService.deleteActor(1L);

        verify(actorRepository).existsById(1L);
        verify(actorRepository).deleteById(1L);
    }

    @Test
    void deleteActor_ShouldThrowExceptionWhenActorNotFound() {

        when(actorRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> {
            actorService.deleteActor(1L);
        });
        verify(actorRepository, never()).deleteById(any());
    }

    @Test
    void saveActorsBulk_ShouldSaveAllValidActors() {

        Movie movie = new Movie();
        movie.setId(1L);

        List<Actor> actors = Arrays.asList(
                createTestActor("Actor 1", movie),
                createTestActor("Actor 2", movie),
                createTestActor("Actor 3", movie)
        );

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(actorRepository.save(any(Actor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Actor> result = actorService.saveActorsBulk(actors);

        assertEquals(3, result.size());
        verify(movieRepository, times(3)).findById(1L);
        verify(actorRepository, times(3)).save(any(Actor.class));
    }

    @Test
    void saveActorsBulk_ShouldFilterInvalidActors() {

        Movie movie = new Movie();
        movie.setId(1L);

        List<Actor> actors = Arrays.asList(
                createTestActor("", movie), // invalid
                createTestActor("Actor 2", movie),
                createTestActor(null, movie) // invalid
        );

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(actorRepository.save(any(Actor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Actor> result = actorService.saveActorsBulk(actors);

        assertEquals(1, result.size());
        assertEquals("Actor 2", result.get(0).getName());
    }

    @Test
    void saveActorsBulk_ShouldThrowExceptionWhenMovieNotFound() {

        Movie movie = new Movie();
        movie.setId(99L);

        List<Actor> actors = Collections.singletonList(
                createTestActor("Actor 1", movie)
        );

        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> actorService.saveActorsBulk(actors));
    }

    private Actor createTestActor(String name, Movie movie) {
        Actor actor = new Actor();
        actor.setName(name);
        actor.setMovie(movie);
        return actor;
    }
}