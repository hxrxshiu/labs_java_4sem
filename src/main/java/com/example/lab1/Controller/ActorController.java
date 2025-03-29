package com.example.lab1.Controller;

import com.example.lab1.Entity.Actor;
import com.example.lab1.Service.ActorService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Actor>> getAllActors() {
        List<Actor> actors = actorService.getAllActors();
        return ResponseEntity.ok(actors);
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<Actor>> getActorsByMovie(@PathVariable Long movieId) {
        List<Actor> actors = actorService.getActorsByMovieId(movieId);
        return ResponseEntity.ok(actors);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Actor>> searchActorsByName(@RequestParam String name) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Actor> actors = actorService.findActorsByNameContaining(name);
        return ResponseEntity.ok(actors);
    }

    @PostMapping("/movie/{movieId}")
    public ResponseEntity<Actor> createActor(
            @RequestBody Actor actor,
            @PathVariable Long movieId
    ) {
        if (actor == null || actor.getName() == null || actor.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Actor savedActor = actorService.saveActor(actor, movieId);
        return ResponseEntity.ok(savedActor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActor(@PathVariable Long id) {
        actorService.deleteActor(id);
        return ResponseEntity.noContent().build();
    }
}