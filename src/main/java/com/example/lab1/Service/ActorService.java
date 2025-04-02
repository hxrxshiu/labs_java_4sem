package com.example.lab1.Service;

import com.example.lab1.Config.CacheNames;
import com.example.lab1.Entity.Actor;
import com.example.lab1.Entity.Movie;
import com.example.lab1.Repository.ActorRepository;
import com.example.lab1.Repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActorService {
    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;

    @Autowired
    public ActorService(ActorRepository actorRepository, MovieRepository movieRepository) {
        this.actorRepository = actorRepository;
        this.movieRepository = movieRepository;
    }

    @Cacheable(value = CacheNames.ACTORS, key = "'all'")
    public List<Actor> getAllActors() {
        System.out.println("Loading actors from DB...");
        return actorRepository.findAll();
    }

    @Cacheable(value = CacheNames.ACTORS, key = "'movie_' + #movieId")
    public List<Actor> getActorsByMovieId(Long movieId) {
        System.out.println("Loading actors for movie " + movieId + " from DB...");
        return actorRepository.findByMovieId(movieId);
    }

    @Cacheable(value = CacheNames.ACTORS, key = "'search_' + #namePart.toLowerCase()")
    public List<Actor> findActorsByNameContaining(String namePart) {
        System.out.println("Searching actors by name: " + namePart);
        return actorRepository.findByNameContainingIgnoreCase(namePart);
    }

    @CacheEvict(value = CacheNames.ACTORS, allEntries = true)
    @Transactional
    public Actor saveActor(Actor actor, Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow();
        actor.setMovie(movie);
        return actorRepository.save(actor);
    }

    @CacheEvict(value = CacheNames.ACTORS, allEntries = true)
    @Transactional
    public void deleteActor(Long id) {
        actorRepository.deleteById(id);
    }
}