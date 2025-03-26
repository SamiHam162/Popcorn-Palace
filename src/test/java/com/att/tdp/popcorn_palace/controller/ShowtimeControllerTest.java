package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.service.ShowtimeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShowtimeController.class)
public class ShowtimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShowtimeService showtimeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Showtime showtime;
    private Instant startTime;
    private Instant endTime;

    @BeforeEach
    void setUp() {
        startTime = Instant.parse("2025-04-01T10:00:00Z");
        endTime = Instant.parse("2025-04-01T12:30:00Z");
        showtime = new Showtime(1L, "Theater 1", startTime, endTime, 12.50);
        showtime.setId(1L);
    }

    @Test
    void getAllShowtimes_ShouldReturnAllShowtimes() throws Exception {
        // Arrange
        List<Showtime> showtimes = Arrays.asList(
                showtime,
                new Showtime(2L, "Theater 2",
                        Instant.parse("2025-04-01T13:00:00Z"),
                        Instant.parse("2025-04-01T15:30:00Z"), 14.00)
        );
        when(showtimeService.getAllShowtimes()).thenReturn(showtimes);

        // Act & Assert
        mockMvc.perform(get("/showtimes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].theater", is("Theater 1")))
                .andExpect(jsonPath("$[1].theater", is("Theater 2")));

        verify(showtimeService, times(1)).getAllShowtimes();
    }

    @Test
    void getShowtimeById_WithExistingId_ShouldReturnShowtime() throws Exception {
        // Arrange
        when(showtimeService.getShowtimeById(1L)).thenReturn(Optional.of(showtime));

        // Act & Assert
        mockMvc.perform(get("/showtimes/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.movieId", is(1)))
                .andExpect(jsonPath("$.theater", is("Theater 1")))
                .andExpect(jsonPath("$.price", is(12.5)));

        verify(showtimeService, times(1)).getShowtimeById(1L);
    }

    @Test
    void getShowtimeById_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(showtimeService.getShowtimeById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/showtimes/99"))
                .andExpect(status().isNotFound());

        verify(showtimeService, times(1)).getShowtimeById(99L);
    }

    @Test
    void createShowtime_WithValidData_ShouldReturnCreatedShowtime() throws Exception {
        // Arrange
        Showtime newShowtime = new Showtime(1L, "Theater 3", startTime, endTime, 15.00);
        when(showtimeService.createShowtime(any(Showtime.class))).thenReturn(newShowtime);

        // Act & Assert
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newShowtime)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.movieId", is(1)))
                .andExpect(jsonPath("$.theater", is("Theater 3")))
                .andExpect(jsonPath("$.price", is(15.0)));

        verify(showtimeService, times(1)).createShowtime(any(Showtime.class));
    }

    @Test
    void createShowtime_WithOverlappingShowtime_ShouldReturnBadRequest() throws Exception {
        // Arrange
        Showtime newShowtime = new Showtime(1L, "Theater 1", startTime, endTime, 15.00);
        when(showtimeService.createShowtime(any(Showtime.class)))
                .thenThrow(new IllegalArgumentException("There is already a showtime scheduled in this theater during the specified time"));

        // Act & Assert
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newShowtime)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("There is already a showtime scheduled")));

        verify(showtimeService, times(1)).createShowtime(any(Showtime.class));
    }

    @Test
    void updateShowtime_WithExistingIdAndValidData_ShouldReturnUpdatedShowtime() throws Exception {
        // Arrange
        Showtime updatedShowtime = new Showtime(1L, "Theater 1 Updated", startTime, endTime, 18.00);
        updatedShowtime.setId(1L);
        when(showtimeService.updateShowtime(eq(1L), any(Showtime.class))).thenReturn(updatedShowtime);

        // Act & Assert
        mockMvc.perform(post("/showtimes/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedShowtime)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.theater", is("Theater 1 Updated")))
                .andExpect(jsonPath("$.price", is(18.0)));

        verify(showtimeService, times(1)).updateShowtime(eq(1L), any(Showtime.class));
    }

    @Test
    void updateShowtime_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        Showtime updatedShowtime = new Showtime(1L, "Theater 1 Updated", startTime, endTime, 18.00);
        when(showtimeService.updateShowtime(eq(99L), any(Showtime.class))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/showtimes/update/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedShowtime)))
                .andExpect(status().isNotFound());

        verify(showtimeService, times(1)).updateShowtime(eq(99L), any(Showtime.class));
    }

    @Test
    void deleteShowtime_WithExistingId_ShouldReturnOk() throws Exception {
        // Arrange
        when(showtimeService.deleteShowtime(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/showtimes/1"))
                .andExpect(status().isOk());

        verify(showtimeService, times(1)).deleteShowtime(1L);
    }

    @Test
    void deleteShowtime_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(showtimeService.deleteShowtime(99L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/showtimes/99"))
                .andExpect(status().isNotFound());

        verify(showtimeService, times(1)).deleteShowtime(99L);
    }
}
