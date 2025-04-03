package com.example.lab1.Controller;

import com.example.lab1.Entity.Actor;
import com.example.lab1.Service.ActorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Actor Management", description = "API for managing actors")
@RestController
@RequestMapping("/api/actors")
@RequiredArgsConstructor
public class ActorController {

    private final ActorService actorService;

    @Operation(summary = "Get all actors")
    @ApiResponse(responseCode = "200", description = "List of all actors")
    @GetMapping
    public ResponseEntity<List<Actor>> getAllActors() {
        return ResponseEntity.ok(actorService.getAllActors());
    }

    @Operation(summary = "Get actors by movie ID")
    @ApiResponse(responseCode = "200", description = "List of actors for specified movie")
    @ApiResponse(responseCode = "404", description = "Movie not found")
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<Actor>> getActorsByMovie(
            @Parameter(description = "ID of the movie",
                    required = true,
                    example = "1")
            @PathVariable Long movieId) {
        return ResponseEntity.ok(actorService.getActorsByMovieId(movieId));
    }

    @Operation(summary = "Search actors by name")
    @ApiResponse(responseCode = "200", description = "List of matching actors")
    @ApiResponse(responseCode = "400", description = "Invalid name parameter")
    @GetMapping("/search")
    public ResponseEntity<List<Actor>> searchActorsByName(
            @Parameter(description = "Name or part of name to search",
                    required = true,
                    example = "John")
            @RequestParam String name) {
        return ResponseEntity.ok(actorService.findActorsByNameContaining(name));
    }

    @Operation(summary = "Create actor for movie")
    @ApiResponse(responseCode = "201", description = "Actor created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Movie not found")
    @PostMapping("/movie/{movieId}")
    public ResponseEntity<Actor> createActor(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Actor data to create",
                    required = true)
            @Valid @RequestBody Actor actor,
            @Parameter(description = "ID of the associated movie",
                    required = true,
                    example = "1")
            @PathVariable Long movieId) {
        return new ResponseEntity<>(actorService.saveActor(actor, movieId), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete actor")
    @ApiResponse(responseCode = "204", description = "Actor deleted successfully")
    @ApiResponse(responseCode = "404", description = "Actor not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActor(
            @Parameter(description = "ID of the actor to delete",
                    required = true,
                    example = "1")
            @PathVariable Long id) {
        actorService.deleteActor(id);
        return ResponseEntity.noContent().build();
    }
}