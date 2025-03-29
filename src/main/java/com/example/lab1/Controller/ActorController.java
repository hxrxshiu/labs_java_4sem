package com.example.lab1.Controller;

import com.example.lab1.Entity.Actor;
import com.example.lab1.Service.ActorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/actors")
public class ActorController {
    private final ActorService actorService;

    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    @GetMapping
    public List<Actor> getAllActors() {
        return actorService.getAllActors();
    }

    @GetMapping("/movie/{movieId}")
    public List<Actor> getActorsByMovie(@PathVariable Long movieId) {
        return actorService.getActorsByMovieId(movieId);
    }

    @PostMapping("/movie/{movieId}")
    public Actor createActor(@RequestBody Actor actor, @PathVariable Long movieId) {
        return actorService.saveActor(actor, movieId);
    }

    @DeleteMapping("/{id}")
    public void deleteActor(@PathVariable Long id) {
        actorService.deleteActor(id);
    }
}