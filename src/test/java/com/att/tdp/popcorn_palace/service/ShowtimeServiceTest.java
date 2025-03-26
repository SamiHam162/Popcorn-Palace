package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShowtimeServiceTest {

    @Mock
    private ShowtimeRepository showtimeRepository;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private ShowtimeService showtimeService;

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
    void getAllShowtimes_ShouldReturnAllShowtimes() {
        // Arrange
        List<Showtime> expectedShowtimes = Arrays.asList(
                showtime,
                new Showtime(2L, "Theater 2",
                        Instant.parse("2025-04-01T13:00:00Z"),
                        Instant.parse("2025-04-01T15:30:00Z"), 14.00)
        );
        when(showtimeRepository.findAll()).thenReturn(expectedShowtimes);

        // Act
        List<Showtime> actualShowtimes = showtimeService.getAllShowtimes();

        // Assert
        assertEquals(expectedShowtimes.size(), actualShowtimes.size());
        assertEquals(expectedShowtimes, actualShowtimes);
        verify(showtimeRepository, times(1)).findAll();
    }

    @Test
    void getShowtimeById_WithExistingId_ShouldReturnShowtime() {
        // Arrange
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));

        // Act
        Optional<Showtime> result = showtimeService.getShowtimeById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(showtime, result.get());
        verify(showtimeRepository, times(1)).findById(1L);
    }

    @Test
    void getShowtimeById_WithNonExistingId_ShouldReturnEmptyOptional() {
        // Arrange
        when(showtimeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Showtime> result = showtimeService.getShowtimeById(99L);

        // Assert
        assertFalse(result.isPresent());
        verify(showtimeRepository, times(1)).findById(99L);
    }

    @Test
    void createShowtime_WithValidData_ShouldSaveAndReturnShowtime() {
        // Arrange
        when(movieRepository.existsById(1L)).thenReturn(true);
        when(showtimeRepository.findOverlappingShowtimes(anyString(), any(Instant.class), any(Instant.class), any()))
                .thenReturn(Collections.emptyList());
        when(showtimeRepository.save(any(Showtime.class))).thenReturn(showtime);

        // Act
        Showtime result = showtimeService.createShowtime(showtime);

        // Assert
        assertEquals(showtime, result);
        verify(movieRepository, times(1)).existsById(1L);
        verify(showtimeRepository, times(1)).findOverlappingShowtimes(
                showtime.getTheater(), showtime.getStartTime(), showtime.getEndTime(), null);
        verify(showtimeRepository, times(1)).save(showtime);
    }

    @Test
    void createShowtime_WithNonExistingMovie_ShouldThrowException() {
        // Arrange
        when(movieRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> showtimeService.createShowtime(showtime));
        assertEquals("Movie with ID 1 does not exist", exception.getMessage());
        verify(movieRepository, times(1)).existsById(1L);
        verify(showtimeRepository, never()).findOverlappingShowtimes(anyString(), any(), any(), any());
        verify(showtimeRepository, never()).save(any());
    }

    @Test
    void createShowtime_WithInvalidTimeRange_ShouldThrowException() {
        // Arrange
        Showtime invalidShowtime = new Showtime(1L, "Theater 1", endTime, startTime, 12.50);
        when(movieRepository.existsById(1L)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> showtimeService.createShowtime(invalidShowtime));
        assertEquals("Start time must be before end time", exception.getMessage());
        verify(movieRepository, times(1)).existsById(1L);
        verify(showtimeRepository, never()).findOverlappingShowtimes(anyString(), any(), any(), any());
        verify(showtimeRepository, never()).save(any());
    }

    @Test
    void createShowtime_WithOverlappingShowtime_ShouldThrowException() {
        // Arrange
        when(movieRepository.existsById(1L)).thenReturn(true);
        when(showtimeRepository.findOverlappingShowtimes(anyString(), any(Instant.class), any(Instant.class), any()))
                .thenReturn(Collections.singletonList(showtime));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> showtimeService.createShowtime(showtime));
        assertEquals("There is already a showtime scheduled in this theater during the specified time", exception.getMessage());
        verify(movieRepository, times(1)).existsById(1L);
        verify(showtimeRepository, times(1)).findOverlappingShowtimes(
                showtime.getTheater(), showtime.getStartTime(), showtime.getEndTime(), null);
        verify(showtimeRepository, never()).save(any());
    }

    @Test
    void updateShowtime_WithValidData_ShouldUpdateAndReturnShowtime() {
        // Arrange
        Showtime updatedShowtime = new Showtime(1L, "Theater 1 Updated", startTime, endTime, 15.00);
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
        when(movieRepository.existsById(1L)).thenReturn(true);
        when(showtimeRepository.findOverlappingShowtimes(anyString(), any(Instant.class), any(Instant.class), any()))
                .thenReturn(Collections.emptyList());
        when(showtimeRepository.save(any(Showtime.class))).thenReturn(updatedShowtime);

        // Act
        Showtime result = showtimeService.updateShowtime(1L, updatedShowtime);

        // Assert
        assertNotNull(result);
        assertEquals(updatedShowtime, result);
        verify(showtimeRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).existsById(1L);
        verify(showtimeRepository, times(1)).findOverlappingShowtimes(
                updatedShowtime.getTheater(), updatedShowtime.getStartTime(), updatedShowtime.getEndTime(), 1L);
        verify(showtimeRepository, times(1)).save(any());
    }

    @Test
    void deleteShowtime_WithExistingId_ShouldReturnTrue() {
        // Arrange
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
        doNothing().when(showtimeRepository).deleteById(1L);

        // Act
        boolean result = showtimeService.deleteShowtime(1L);

        // Assert
        assertTrue(result);
        verify(showtimeRepository, times(1)).findById(1L);
        verify(showtimeRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteShowtime_WithNonExistingId_ShouldReturnFalse() {
        // Arrange
        when(showtimeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        boolean result = showtimeService.deleteShowtime(99L);

        // Assert
        assertFalse(result);
        verify(showtimeRepository, times(1)).findById(99L);
        verify(showtimeRepository, never()).deleteById(any());
    }
}

