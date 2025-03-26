package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie movie;

    @BeforeEach
    void setUp() {
        movie = new Movie("Test Movie", "Action", 120, "PG-13", 2023);
        movie.setId(1L);
    }

    @Test
    void getAllMovies_ShouldReturnAllMovies() {
        // Arrange
        List<Movie> expectedMovies = Arrays.asList(
                movie,
                new Movie("Another Movie", "Comedy", 90, "PG", 2022)
        );
        when(movieRepository.findAll()).thenReturn(expectedMovies);

        // Act
        List<Movie> actualMovies = movieService.getAllMovies();

        // Assert
        assertEquals(expectedMovies.size(), actualMovies.size());
        assertEquals(expectedMovies, actualMovies);
        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void getMovieById_WithExistingId_ShouldReturnMovie() {
        // Arrange
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        // Act
        Optional<Movie> result = movieService.getMovieById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(movie, result.get());
        verify(movieRepository, times(1)).findById(1L);
    }

    @Test
    void getMovieByTitle_WithExistingTitle_ShouldReturnMovie() {
        // Arrange
        when(movieRepository.findByTitle("Test Movie")).thenReturn(Optional.of(movie));

        // Act
        Optional<Movie> result = movieService.getMovieByTitle("Test Movie");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(movie, result.get());
        verify(movieRepository, times(1)).findByTitle("Test Movie");
    }

    @Test
    void getMovieByTitle_WithNonExistingTitle_ShouldReturnEmptyOptional() {
        // Arrange
        when(movieRepository.findByTitle("Non Existing Movie")).thenReturn(Optional.empty());

        // Act
        Optional<Movie> result = movieService.getMovieByTitle("Non Existing Movie");

        // Assert
        assertFalse(result.isPresent());
        verify(movieRepository, times(1)).findByTitle("Non Existing Movie");
    }

    @Test
    void createMovie_ShouldSaveAndReturnMovie() {
        // Arrange
        Movie newMovie = new Movie("New Movie", "Drama", 110, "R", 2024);
        when(movieRepository.save(any(Movie.class))).thenReturn(newMovie);

        // Act
        Movie result = movieService.createMovie(newMovie);

        // Assert
        assertEquals(newMovie, result);
        verify(movieRepository, times(1)).save(newMovie);
    }

    @Test
    void updateMovieByTitle_WithExistingTitle_ShouldReturnTrue() {
        // Arrange
        Movie updatedMovie = new Movie("Updated Movie", "Sci-Fi", 130, "PG-13", 2025);
        when(movieRepository.findByTitle("Test Movie")).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenReturn(updatedMovie);

        // Act
        boolean result = movieService.updateMovieByTitle("Test Movie", updatedMovie);

        // Assert
        assertTrue(result);
        verify(movieRepository, times(1)).findByTitle("Test Movie");
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void updateMovieByTitle_WithNonExistingTitle_ShouldReturnFalse() {
        // Arrange
        Movie updatedMovie = new Movie("Updated Movie", "Sci-Fi", 130, "PG-13", 2025);
        when(movieRepository.findByTitle("Non Existing Movie")).thenReturn(Optional.empty());

        // Act
        boolean result = movieService.updateMovieByTitle("Non Existing Movie", updatedMovie);

        // Assert
        assertFalse(result);
        verify(movieRepository, times(1)).findByTitle("Non Existing Movie");
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void deleteMovieByTitle_WithExistingTitle_ShouldReturnTrue() {
        // Arrange
        when(movieRepository.findByTitle("Test Movie")).thenReturn(Optional.of(movie));
        doNothing().when(movieRepository).delete(movie);

        // Act
        boolean result = movieService.deleteMovieByTitle("Test Movie");

        // Assert
        assertTrue(result);
        verify(movieRepository, times(1)).findByTitle("Test Movie");
        verify(movieRepository, times(1)).delete(movie);
    }

    @Test
    void deleteMovieByTitle_WithNonExistingTitle_ShouldReturnFalse() {
        // Arrange
        when(movieRepository.findByTitle("Non Existing Movie")).thenReturn(Optional.empty());

        // Act
        boolean result = movieService.deleteMovieByTitle("Non Existing Movie");

        // Assert
        assertFalse(result);
        verify(movieRepository, times(1)).findByTitle("Non Existing Movie");
        verify(movieRepository, never()).delete(any());
    }
}
