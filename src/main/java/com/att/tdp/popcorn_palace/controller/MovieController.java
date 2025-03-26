package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Get all movies
     * @return List of all movies
     */
    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    /**
     * Get movie by id
     * @param id Movie id
     * @return Movie if found, 404 Not Found otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Optional<Movie> movie = movieService.getMovieById(id);
        return movie.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Create a new movie
     * @param movie Movie to create
     * @return Created movie with generated id
     */
    @PostMapping
    public ResponseEntity<?> createMovie(@Valid @RequestBody Movie movie) {
        Movie createdMovie = movieService.createMovie(movie);
        return new ResponseEntity<>(createdMovie, HttpStatus.OK);
    }

    /**
     * Update an existing movie by title
     * @param movieTitle Movie title to update
     * @param movieDetails Updated movie details
     * @return 200 OK if updated, 404 Not Found otherwise
     */
    @PostMapping("/update/{movieTitle}")
    public ResponseEntity<Void> updateMovie(@PathVariable String movieTitle, @Valid @RequestBody Movie movieDetails) {
        boolean updated = movieService.updateMovieByTitle(movieTitle, movieDetails);
        if (updated) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Delete a movie by title
     * @param movieTitle Movie title to delete
     * @return 200 OK if deleted, 404 Not Found otherwise
     */
    @DeleteMapping("/{movieTitle}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String movieTitle) {
        boolean deleted = movieService.deleteMovieByTitle(movieTitle);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

