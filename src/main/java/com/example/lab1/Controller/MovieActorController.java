package com.example.lab1.Controller;

import com.example.lab1.Entity.Actor;
import com.example.lab1.Service.MovieActorService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Movie-Actor Management",
        description = "API for managing relationships between movies and actors")
@RestController
@RequestMapping("/api/movies/{movieId}/actors")
@RequiredArgsConstructor
public class MovieActorController {

    private final MovieActorService movieActorService;

    @Operation(summary = "Add actor to movie",
            description = "Creates new actor and associates it with specified movie")
    @ApiResponse(responseCode = "201", description = "Actor added successfully")
    @ApiResponse(responseCode = "400", description = "Invalid actor data")
    @ApiResponse(responseCode = "404", description = "Movie not found")
    @PostMapping
    public ResponseEntity<Actor> addActorToMovie(
            @Parameter(description = "ID of the movie to add actor to",
                    required = true,
                    example = "1")
            @PathVariable Long movieId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Actor data to create and associate",
                    required = true)
            @Valid @RequestBody Actor actorRequest) {

        Actor createdActor = movieActorService.addActorToMovie(movieId, actorRequest);
        return new ResponseEntity<>(createdActor, HttpStatus.CREATED);
    }
}