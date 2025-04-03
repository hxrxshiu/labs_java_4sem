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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieActorService {

    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;

    @CacheEvict(value = CacheNames.ACTORS, allEntries = true)
    @Transactional
    public Actor addActorToMovie(Long movieId, Actor actorRequest) {
        log.info("Adding actor to movie ID: {}", movieId);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with id: " + movieId));

        Actor actor = new Actor();
        actor.setName(actorRequest.getName());
        actor.setMovie(movie);

        return actorRepository.save(actor);
    }
}