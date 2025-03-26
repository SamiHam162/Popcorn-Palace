package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.model.Booking;
import com.att.tdp.popcorn_palace.repository.BookingRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ShowtimeRepository showtimeRepository;

    @InjectMocks
    private BookingService bookingService;

    private Booking booking;
    private String bookingId;

    @BeforeEach
    void setUp() {
        bookingId = UUID.randomUUID().toString();
        booking = new Booking(bookingId, 1L, "84438967-f68f-4fa0-b620-0f08217e76af", 15);
    }

    @Test
    void getAllBookings_ShouldReturnAllBookings() {
        // Arrange
        List<Booking> expectedBookings = Arrays.asList(
                booking,
                new Booking(UUID.randomUUID().toString(), 1L, "84438967-f68f-4fa0-b620-0f08217e76af", 16)
        );
        when(bookingRepository.findAll()).thenReturn(expectedBookings);

        // Act
        List<Booking> actualBookings = bookingService.getAllBookings();

        // Assert
        assertEquals(expectedBookings.size(), actualBookings.size());
        assertEquals(expectedBookings, actualBookings);
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void getBookingById_WithExistingId_ShouldReturnBooking() {
        // Arrange
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // Act
        Optional<Booking> result = bookingService.getBookingById(bookingId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(booking, result.get());
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getBookingById_WithNonExistingId_ShouldReturnEmptyOptional() {
        // Arrange
        String nonExistingId = UUID.randomUUID().toString();
        when(bookingRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act
        Optional<Booking> result = bookingService.getBookingById(nonExistingId);

        // Assert
        assertFalse(result.isPresent());
        verify(bookingRepository, times(1)).findById(nonExistingId);
    }

    @Test
    void getBookingsByShowtimeId_ShouldReturnBookingsForShowtime() {
        // Arrange
        List<Booking> expectedBookings = Arrays.asList(
                booking,
                new Booking(UUID.randomUUID().toString(), 1L, "84438967-f68f-4fa0-b620-0f08217e76af", 16)
        );
        when(bookingRepository.findByShowtimeId(1L)).thenReturn(expectedBookings);

        // Act
        List<Booking> actualBookings = bookingService.getBookingsByShowtimeId(1L);

        // Assert
        assertEquals(expectedBookings.size(), actualBookings.size());
        assertEquals(expectedBookings, actualBookings);
        verify(bookingRepository, times(1)).findByShowtimeId(1L);
    }

    @Test
    void createBooking_WithValidData_ShouldSaveAndReturnBooking() {
        // Arrange
        when(showtimeRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.existsByShowtimeIdAndSeatNumber(1L, 15)).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // Act
        Booking result = bookingService.createBooking(booking);

        // Assert
        assertEquals(booking, result);
        verify(showtimeRepository, times(1)).existsById(1L);
        verify(bookingRepository, times(1)).existsByShowtimeIdAndSeatNumber(1L, 15);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void createBooking_WithNonExistingShowtime_ShouldThrowException() {
        // Arrange
        when(showtimeRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(booking));
        assertEquals("Showtime with ID 1 does not exist", exception.getMessage());
        verify(showtimeRepository, times(1)).existsById(1L);
        verify(bookingRepository, never()).existsByShowtimeIdAndSeatNumber(anyLong(), anyInt());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_WithAlreadyBookedSeat_ShouldThrowException() {
        // Arrange
        when(showtimeRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.existsByShowtimeIdAndSeatNumber(1L, 15)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(booking));
        assertEquals("Seat 15 is already booked for this showtime", exception.getMessage());
        verify(showtimeRepository, times(1)).existsById(1L);
        verify(bookingRepository, times(1)).existsByShowtimeIdAndSeatNumber(1L, 15);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_WithNullId_ShouldGenerateIdAndSave() {
        // Arrange
        Booking bookingWithoutId = new Booking(null, 1L, "84438967-f68f-4fa0-b620-0f08217e76af", 15);
        when(showtimeRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.existsByShowtimeIdAndSeatNumber(1L, 15)).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking savedBooking = invocation.getArgument(0);
            assertNotNull(savedBooking.getId());
            return savedBooking;
        });

        // Act
        Booking result = bookingService.createBooking(bookingWithoutId);

        // Assert
        assertNotNull(result.getId());
        verify(showtimeRepository, times(1)).existsById(1L);
        verify(bookingRepository, times(1)).existsByShowtimeIdAndSeatNumber(1L, 15);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void deleteBooking_WithExistingId_ShouldReturnTrue() {
        // Arrange
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        doNothing().when(bookingRepository).deleteById(bookingId);

        // Act
        boolean result = bookingService.deleteBooking(bookingId);

        // Assert
        assertTrue(result);
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(1)).deleteById(bookingId);
    }

    @Test
    void deleteBooking_WithNonExistingId_ShouldReturnFalse() {
        // Arrange
        String nonExistingId = UUID.randomUUID().toString();
        when(bookingRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act
        boolean result = bookingService.deleteBooking(nonExistingId);

        // Assert
        assertFalse(result);
        verify(bookingRepository, times(1)).findById(nonExistingId);
        verify(bookingRepository, never()).deleteById(any());
    }
}

