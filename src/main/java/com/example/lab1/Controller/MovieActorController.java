package com.example.lab1.Controller;

import com.example.lab1.Entity.Actor;
import com.example.lab1.Service.MovieActorService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movies/{movieId}/actors")
public class MovieActorController {

    private final MovieActorService movieActorService;

    public MovieActorController(MovieActorService movieActorService) {
        this.movieActorService = movieActorService;
    }

    @PostMapping
    public Actor addActorToMovie(
            @PathVariable Long movieId,
            @RequestBody Actor actorRequest
    ) {
        return movieActorService.addActorToMovie(movieId, actorRequest);
    }
}