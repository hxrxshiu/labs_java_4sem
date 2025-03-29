package com.example.lab1.Service;

import com.example.lab1.Entity.Actor;
import com.example.lab1.Entity.Movie;
import com.example.lab1.Repository.ActorRepository;
import com.example.lab1.Repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActorService {
    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;

    public ActorService(ActorRepository actorRepository, MovieRepository movieRepository) {
        this.actorRepository = actorRepository;
        this.movieRepository = movieRepository;
    }

    public List<Actor> getAllActors() {
        return actorRepository.findAll();
    }

    public List<Actor> getActorsByMovieId(Long movieId) {
        return actorRepository.findByMovieId(movieId);
    }

    @Transactional
    public Actor saveActor(Actor actor, Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow();
        actor.setMovie(movie);
        return actorRepository.save(actor);
    }

    @Transactional
    public void deleteActor(Long id) {
        actorRepository.deleteById(id);
    }
}