package com.example.lab1.Service;

import com.example.lab1.Entity.Actor;
import com.example.lab1.Entity.Movie;
import com.example.lab1.Repository.ActorRepository;
import com.example.lab1.Repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovieActorService {
    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;

    public MovieActorService(ActorRepository actorRepository, MovieRepository movieRepository) {
        this.actorRepository = actorRepository;
        this.movieRepository = movieRepository;
    }

    @Transactional
    public Actor addActorToMovie(Long movieId, Actor actorRequest) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + movieId));

        Actor actor = new Actor();
        actor.setName(actorRequest.getName());
        actor.setMovie(movie);

        return actorRepository.save(actor);
    }
}