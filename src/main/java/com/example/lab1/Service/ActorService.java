package com.example.lab1.Service;

import com.example.lab1.Entity.Actor;
import com.example.lab1.Entity.Movie;
import com.example.lab1.Repository.ActorRepository;
import com.example.lab1.Repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ActorService {
    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;
    private final ConcurrentHashMap<String, Object> cache;

    @Autowired
    public ActorService(ActorRepository actorRepository,
                        MovieRepository movieRepository,
                        ConcurrentHashMap<String, Object> inMemoryCache) {
        this.actorRepository = actorRepository;
        this.movieRepository = movieRepository;
        this.cache = inMemoryCache;
    }

    @SuppressWarnings("unchecked")
    private List<Actor> getCachedActors(String cacheKey) {
        Object cached = cache.get(cacheKey);
        if (cached instanceof List<?>) {
            try {
                return (List<Actor>) cached;
            } catch (ClassCastException e) {

                cache.remove(cacheKey);
            }
        }
        return null;
    }

    public List<Actor> getAllActors() {
        String cacheKey = "all_actors";
        List<Actor> cached = getCachedActors(cacheKey);
        if (cached != null) {
            return cached;
        }
        List<Actor> actors = actorRepository.findAll();
        cache.put(cacheKey, actors);
        return actors;
    }

    public List<Actor> getActorsByMovieId(Long movieId) {
        String cacheKey = "actors_movie_" + movieId;
        List<Actor> cached = getCachedActors(cacheKey);
        if (cached != null) {
            return cached;
        }
        List<Actor> actors = actorRepository.findByMovieId(movieId);
        cache.put(cacheKey, actors);
        return actors;
    }

    public List<Actor> findActorsByNameContaining(String namePart) {
        String cacheKey = "actors_search_" + namePart.toLowerCase();
        List<Actor> cached = getCachedActors(cacheKey);
        if (cached != null) {
            return cached;
        }
        List<Actor> actors = actorRepository.findByNameContainingIgnoreCase(namePart);
        cache.put(cacheKey, actors);
        return actors;
    }

    @Transactional
    public Actor saveActor(Actor actor, Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow();
        actor.setMovie(movie);
        Actor savedActor = actorRepository.save(actor);
        clearCacheForMovie(movieId);
        return savedActor;
    }

    @Transactional
    public void deleteActor(Long id) {
        Actor actor = actorRepository.findById(id).orElseThrow();
        Long movieId = actor.getMovie().getId();
        actorRepository.deleteById(id);
        clearCacheForMovie(movieId);
    }

    private void clearCacheForMovie(Long movieId) {
        cache.remove("all_actors");
        cache.remove("actors_movie_" + movieId);

        cache.keySet().removeIf(key -> key.startsWith("actors_search_"));
    }
}