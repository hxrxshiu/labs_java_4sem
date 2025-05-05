package com.example.lab1.Service;

import com.example.lab1.Entity.Actor;
import com.example.lab1.Entity.Movie;
import com.example.lab1.Exception.EntityNotFoundException;
import com.example.lab1.Repository.ActorRepository;
import com.example.lab1.Repository.MovieRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActorServiceTest {

    @Mock
    private ActorRepository actorRepository;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private ActorService actorService;

    @Test
    void shouldReturnAllActors() {
        Actor actor = mock(Actor.class);
        when(actor.getName()).thenReturn("Test Actor");
        when(actorRepository.findAll()).thenReturn(Collections.singletonList(actor));

        List<Actor> result = actorService.getAllActors();

        assertEquals(1, result.size());
        assertEquals("Test Actor", result.get(0).getName());
        verify(actorRepository).findAll();
    }

    @Test
    void shouldReturnActorsForMovieId() {
        Actor actor = mock(Actor.class);
        Movie movie = mock(Movie.class);
        when(movie.getId()).thenReturn(1L);
        when(actor.getMovie()).thenReturn(movie);
        when(actorRepository.findByMovieId(1L)).thenReturn(Collections.singletonList(actor));

        List<Actor> result = actorService.getActorsByMovieId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getMovie().getId());
        verify(actorRepository).findByMovieId(1L);
    }

    @Test
    void shouldReturnActorsByNameContaining() {
        Actor actor = mock(Actor.class);
        when(actor.getName()).thenReturn("Test Actor");
        when(actorRepository.findByNameContainingIgnoreCase("test")).thenReturn(Collections.singletonList(actor));

        List<Actor> result = actorService.findActorsByNameContaining("test");

        assertEquals(1, result.size());
        assertEquals("Test Actor", result.get(0).getName());
        verify(actorRepository).findByNameContainingIgnoreCase("test");
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> actorService.findActorsByNameContaining(""));
    }

    @Test
    void shouldSaveActorWhenMovieExists() {
        Actor actor = new Actor();
        actor.setName("Actor");
        Movie movie = new Movie();
        movie.setId(1L);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(actorRepository.save(any(Actor.class))).thenReturn(actor);

        Actor result = actorService.saveActor(actor, 1L);

        assertNotNull(result);
        assertEquals("Actor", result.getName());
        verify(movieRepository).findById(1L);
        verify(actorRepository).save(any(Actor.class));
    }

    @Test
    void shouldThrowExceptionWhenMovieNotFoundDuringSave() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        Actor actor = new Actor();
        actor.setName("Actor");

        assertThrows(EntityNotFoundException.class, () -> actorService.saveActor(actor, 99L));
    }

    @Test
    void shouldDeleteActorWhenExists() {
        when(actorRepository.existsById(1L)).thenReturn(true);

        actorService.deleteActor(1L);

        verify(actorRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenActorNotFoundDuringDelete() {
        when(actorRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> actorService.deleteActor(1L));
        verify(actorRepository, never()).deleteById(any());
    }

    @Test
    void shouldSaveActorsInBulkWhenAllAreValid() {
        Movie movie = new Movie();
        movie.setId(1L);
        List<Actor> actors = Arrays.asList(
                createTestActor("Actor 1", movie),
                createTestActor("Actor 2", movie)
        );

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(actorRepository.save(any(Actor.class))).thenAnswer(i -> i.getArgument(0));

        List<Actor> result = actorService.saveActorsBulk(actors);

        assertEquals(2, result.size());
        verify(actorRepository, times(2)).save(any());
    }

    @Test
    void shouldFilterInvalidActorsWhenSavingInBulk() {
        Movie movie = new Movie();
        movie.setId(1L);
        List<Actor> actors = Arrays.asList(
                createTestActor(null, movie),
                createTestActor("Valid Actor", movie),
                createTestActor("", movie)
        );

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(actorRepository.save(any(Actor.class))).thenAnswer(i -> i.getArgument(0));

        List<Actor> result = actorService.saveActorsBulk(actors);

        assertEquals(1, result.size());
        assertEquals("Valid Actor", result.get(0).getName());
    }

    @Test
    void shouldThrowExceptionWhenMovieNotFoundDuringBulkSave() {
        Movie movie = new Movie();
        movie.setId(99L);
        List<Actor> actors = Collections.singletonList(createTestActor("Actor", movie));

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
