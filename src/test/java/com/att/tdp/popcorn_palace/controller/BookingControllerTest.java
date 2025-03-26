package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.model.Booking;
import com.att.tdp.popcorn_palace.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private Booking booking;
    private String bookingId;

    @BeforeEach
    void setUp() {
        bookingId = "d1a6423b-4469-4b00-8c5f-e3cfc42eacae";
        booking = new Booking(bookingId, 1L, "84438967-f68f-4fa0-b620-0f08217e76af", 15);
    }

    @Test
    void getAllBookings_ShouldReturnAllBookings() throws Exception {
        // Arrange
        List<Booking> bookings = Arrays.asList(
                booking,
                new Booking(UUID.randomUUID().toString(), 1L, "84438967-f68f-4fa0-b620-0f08217e76af", 16)
        );
        when(bookingService.getAllBookings()).thenReturn(bookings);

        // Act & Assert
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(bookingId)))
                .andExpect(jsonPath("$[0].seatNumber", is(15)))
                .andExpect(jsonPath("$[1].seatNumber", is(16)));

        verify(bookingService, times(1)).getAllBookings();
    }

    @Test
    void getBookingById_WithExistingId_ShouldReturnBooking() throws Exception {
        // Arrange
        when(bookingService.getBookingById(bookingId)).thenReturn(Optional.of(booking));

        // Act & Assert
        mockMvc.perform(get("/bookings/" + bookingId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingId)))
                .andExpect(jsonPath("$.showtimeId", is(1)))
                .andExpect(jsonPath("$.userId", is("84438967-f68f-4fa0-b620-0f08217e76af")))
                .andExpect(jsonPath("$.seatNumber", is(15)));

        verify(bookingService, times(1)).getBookingById(bookingId);
    }

    @Test
    void getBookingById_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        String nonExistingId = UUID.randomUUID().toString();
        when(bookingService.getBookingById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/bookings/" + nonExistingId))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).getBookingById(nonExistingId);
    }

    @Test
    void getBookingsByShowtimeId_ShouldReturnBookingsForShowtime() throws Exception {
        // Arrange
        List<Booking> bookings = Arrays.asList(
                booking,
                new Booking(UUID.randomUUID().toString(), 1L, "84438967-f68f-4fa0-b620-0f08217e76af", 16)
        );
        when(bookingService.getBookingsByShowtimeId(1L)).thenReturn(bookings);

        // Act & Assert
        mockMvc.perform(get("/bookings/showtime/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(bookingId)))
                .andExpect(jsonPath("$[0].seatNumber", is(15)))
                .andExpect(jsonPath("$[1].seatNumber", is(16)));

        verify(bookingService, times(1)).getBookingsByShowtimeId(1L);
    }

    @Test
    void createBooking_WithValidData_ShouldReturnBookingId() throws Exception {
        // Arrange
        when(bookingService.createBooking(any(Booking.class))).thenReturn(booking);

        // Act & Assert
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bookingId", is(bookingId)));

        verify(bookingService, times(1)).createBooking(any(Booking.class));
    }

    @Test
    void createBooking_WithAlreadyBookedSeat_ShouldReturnBadRequest() throws Exception {
        // Arrange
        when(bookingService.createBooking(any(Booking.class)))
                .thenThrow(new IllegalArgumentException("Seat 15 is already booked for this showtime"));

        // Act & Assert
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Seat 15 is already booked")));

        verify(bookingService, times(1)).createBooking(any(Booking.class));
    }

    @Test
    void deleteBooking_WithExistingId_ShouldReturnOk() throws Exception {
        // Arrange
        when(bookingService.deleteBooking(bookingId)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/bookings/" + bookingId))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).deleteBooking(bookingId);
    }

    @Test
    void deleteBooking_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        String nonExistingId = UUID.randomUUID().toString();
        when(bookingService.deleteBooking(nonExistingId)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/bookings/" + nonExistingId))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).deleteBooking(nonExistingId);
    }
}
