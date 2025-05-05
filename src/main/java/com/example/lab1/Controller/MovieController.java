package com.example.lab1.Controller;

import com.example.lab1.Entity.Movie;
import com.example.lab1.Service.MovieService;
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

@Tag(name = "Movie Management", description = "API for managing movies")
@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @Operation(summary = "Search movie info",
            description = "Fetches movie data from external API")
    @ApiResponse(responseCode = "200", description = "Movie info retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid title parameter")
    @GetMapping("/search")
    public ResponseEntity<String> searchMovie(
            @Parameter(description = "Movie title to search", required = true, example = "Inception")
            @RequestParam String title) {
        return ResponseEntity.ok(movieService.getMovieInfoByTitle(title));
    }

    @Operation(summary = "Get all movies")
    @ApiResponse(responseCode = "200", description = "List of all movies")
    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @Operation(summary = "Create a new movie")
    @ApiResponse(responseCode = "201", description = "Movie created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid movie data")
    @PostMapping
    public ResponseEntity<Movie> createMovie(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Movie data to create",
                    required = true)
            @Valid @RequestBody Movie movie) {
        return new ResponseEntity<>(movieService.saveMovie(movie), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete a movie")
    @ApiResponse(responseCode = "204", description = "Movie deleted successfully")
    @ApiResponse(responseCode = "404", description = "Movie not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(
            @Parameter(description = "ID of the movie to delete", required = true, example = "1")
            @PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Create multiple movies")
    @ApiResponse(responseCode = "201", description = "Movies created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid movie data")
    @PostMapping("/bulk")
    public ResponseEntity<List<Movie>> createMoviesBulk(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "List of movie data to create",
                    required = true)
            @Valid @RequestBody List<Movie> movies) {
        return new ResponseEntity<>(movieService.saveMoviesBulk(movies), HttpStatus.CREATED);
    }
}
