package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    /**
     * Get all movies
     * @return List of all movies
     */
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    /**
     * Get movie by id
     * @param id Movie id
     * @return Movie if found, empty Optional otherwise
     */
    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }

    /**
     * Get movie by title
     * @param title Movie title
     * @return Movie if found, empty Optional otherwise
     */
    public Optional<Movie> getMovieByTitle(String title) {
        return movieRepository.findByTitle(title);
    }

    /**
     * Create a new movie
     * @param movie Movie to create
     * @return Created movie with generated id
     */
    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    /**
     * Update an existing movie
     * @param id Movie id to update
     * @param movieDetails Updated movie details
     * @return Updated movie if found, null otherwise
     */
    public Movie updateMovie(Long id, Movie movieDetails) {
        Optional<Movie> movie = movieRepository.findById(id);
        if (movie.isPresent()) {
            Movie existingMovie = movie.get();
            existingMovie.setTitle(movieDetails.getTitle());
            existingMovie.setGenre(movieDetails.getGenre());
            existingMovie.setDuration(movieDetails.getDuration());
            existingMovie.setRating(movieDetails.getRating());
            existingMovie.setReleaseYear(movieDetails.getReleaseYear());
            return movieRepository.save(existingMovie);
        }
        return null;
    }

    /**
     * Update an existing movie by title
     * @param title Movie title to update
     * @param movieDetails Updated movie details
     * @return true if updated, false if not found
     */
    public boolean updateMovieByTitle(String title, Movie movieDetails) {
        Optional<Movie> movie = movieRepository.findByTitle(title);
        if (movie.isPresent()) {
            Movie existingMovie = movie.get();
            existingMovie.setTitle(movieDetails.getTitle());
            existingMovie.setGenre(movieDetails.getGenre());
            existingMovie.setDuration(movieDetails.getDuration());
            existingMovie.setRating(movieDetails.getRating());
            existingMovie.setReleaseYear(movieDetails.getReleaseYear());
            movieRepository.save(existingMovie);
            return true;
        }
        return false;
    }

    /**
     * Delete a movie by id
     * @param id Movie id to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteMovie(Long id) {
        Optional<Movie> movie = movieRepository.findById(id);
        if (movie.isPresent()) {
            movieRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Delete a movie by title
     * @param title Movie title to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteMovieByTitle(String title) {
        Optional<Movie> movie = movieRepository.findByTitle(title);
        if (movie.isPresent()) {
            movieRepository.delete(movie.get());
            return true;
        }
        return false;
    }
}

