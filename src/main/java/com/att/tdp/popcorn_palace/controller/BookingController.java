package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.model.Booking;
import com.att.tdp.popcorn_palace.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Get all bookings
     * @return List of all bookings
     */
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    /**
     * Get booking by id
     * @param id Booking id
     * @return Booking if found, 404 Not Found otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable String id) {
        Optional<Booking> booking = bookingService.getBookingById(id);
        return booking.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Get all bookings for a specific showtime
     * @param showtimeId Showtime id
     * @return List of bookings for the showtime
     */
    @GetMapping("/showtime/{showtimeId}")
    public ResponseEntity<List<Booking>> getBookingsByShowtimeId(@PathVariable Long showtimeId) {
        List<Booking> bookings = bookingService.getBookingsByShowtimeId(showtimeId);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    /**
     * Create a new booking
     * @param booking Booking to create
     * @return Created booking with generated id
     */
    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody Booking booking) {
        Booking createdBooking = bookingService.createBooking(booking);
        Map<String, String> response = new HashMap<>();
        response.put("bookingId", createdBooking.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Delete a booking by id
     * @param id Booking id to delete
     * @return 200 OK if deleted, 404 Not Found otherwise
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable String id) {
        boolean deleted = bookingService.deleteBooking(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

