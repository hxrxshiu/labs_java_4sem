package com.example.lab1.Service;

import com.example.lab1.Config.CacheNames;
import com.example.lab1.Entity.Actor;
import com.example.lab1.Entity.Movie;
import com.example.lab1.Exception.EntityNotFoundException;
import com.example.lab1.Repository.ActorRepository;
import com.example.lab1.Repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActorService {

    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;

    @Cacheable(value = CacheNames.ACTORS, key = "'all'")
    public List<Actor> getAllActors() {
        log.debug("Fetching all actors from database");
        return actorRepository.findAll();
    }

    @Cacheable(value = CacheNames.ACTORS, key = "'movie_' + #movieId")
    public List<Actor> getActorsByMovieId(Long movieId) {
        log.debug("Fetching actors for movie ID: {}", movieId);
        return actorRepository.findByMovieId(movieId);
    }

    @Cacheable(value = CacheNames.ACTORS, key = "'search_' + #namePart.toLowerCase()")
    public List<Actor> findActorsByNameContaining(String namePart) {
        log.debug("Searching actors containing name: {}", namePart);
        if (namePart == null || namePart.trim().isEmpty()) {
            throw new IllegalArgumentException("Name parameter cannot be empty");
        }
        return actorRepository.findByNameContainingIgnoreCase(namePart);
    }

    @CacheEvict(value = CacheNames.ACTORS, allEntries = true)
    @Transactional
    public Actor saveActor(Actor actor, Long movieId) {
        log.info("Saving actor: {} for movie ID: {}", actor.getName(), movieId);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with id: " + movieId));
        actor.setMovie(movie);
        return actorRepository.save(actor);
    }

    @CacheEvict(value = CacheNames.ACTORS, allEntries = true)
    @Transactional
    public void deleteActor(Long id) {
        log.info("Deleting actor with ID: {}", id);
        if (!actorRepository.existsById(id)) {
            throw new EntityNotFoundException("Actor not found with id: " + id);
        }
        actorRepository.deleteById(id);
    }
}