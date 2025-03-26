package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @Autowired
    private ObjectMapper objectMapper;

    private Movie movie;

    @BeforeEach
    void setUp() {
        movie = new Movie("Test Movie", "Action", 120, "PG-13", 2023);
        movie.setId(1L);
    }

    @Test
    void getAllMovies_ShouldReturnAllMovies() throws Exception {
        // Arrange
        List<Movie> movies = Arrays.asList(
                movie,
                new Movie("Another Movie", "Comedy", 90, "PG", 2022)
        );
        when(movieService.getAllMovies()).thenReturn(movies);

        // Act & Assert
        mockMvc.perform(get("/movies/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Movie")))
                .andExpect(jsonPath("$[1].title", is("Another Movie")));

        verify(movieService, times(1)).getAllMovies();
    }

    @Test
    void createMovie_WithValidData_ShouldReturnCreatedMovie() throws Exception {
        // Arrange
        Movie newMovie = new Movie("New Movie", "Drama", 110, "R", 2024);
        when(movieService.createMovie(any(Movie.class))).thenReturn(newMovie);

        // Act & Assert
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMovie)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is("New Movie")))
                .andExpect(jsonPath("$.genre", is("Drama")))
                .andExpect(jsonPath("$.duration", is(110)))
                .andExpect(jsonPath("$.rating", is("R")))
                .andExpect(jsonPath("$.releaseYear", is(2024)));

        verify(movieService, times(1)).createMovie(any(Movie.class));
    }

    @Test
    void createMovie_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange
        Movie invalidMovie = new Movie("", "", -10, "", -2000);

        // Act & Assert
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isBadRequest());

        verify(movieService, never()).createMovie(any(Movie.class));
    }

    @Test
    void updateMovie_WithExistingTitleAndValidData_ShouldReturnOk() throws Exception {
        // Arrange
        Movie updatedMovie = new Movie("Updated Movie", "Sci-Fi", 130, "PG-13", 2025);
        when(movieService.updateMovieByTitle(eq("Test Movie"), any(Movie.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/movies/update/Test Movie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMovie)))
                .andExpect(status().isOk());

        verify(movieService, times(1)).updateMovieByTitle(eq("Test Movie"), any(Movie.class));
    }

    @Test
    void updateMovie_WithNonExistingTitle_ShouldReturnNotFound() throws Exception {
        // Arrange
        Movie updatedMovie = new Movie("Updated Movie", "Sci-Fi", 130, "PG-13", 2025);
        when(movieService.updateMovieByTitle(eq("Non Existing Movie"), any(Movie.class))).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/movies/update/Non Existing Movie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMovie)))
                .andExpect(status().isNotFound());

        verify(movieService, times(1)).updateMovieByTitle(eq("Non Existing Movie"), any(Movie.class));
    }

    @Test
    void deleteMovie_WithExistingTitle_ShouldReturnOk() throws Exception {
        // Arrange
        when(movieService.deleteMovieByTitle("Test Movie")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/movies/Test Movie"))
                .andExpect(status().isOk());

        verify(movieService, times(1)).deleteMovieByTitle("Test Movie");
    }

    @Test
    void deleteMovie_WithNonExistingTitle_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(movieService.deleteMovieByTitle("Non Existing Movie")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/movies/Non Existing Movie"))
                .andExpect(status().isNotFound());

        verify(movieService, times(1)).deleteMovieByTitle("Non Existing Movie");
    }
}

