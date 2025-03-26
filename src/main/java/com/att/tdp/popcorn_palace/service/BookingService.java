package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.model.Booking;
import com.att.tdp.popcorn_palace.repository.BookingRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, ShowtimeRepository showtimeRepository) {
        this.bookingRepository = bookingRepository;
        this.showtimeRepository = showtimeRepository;
    }

    /**
     * Get all bookings
     * @return List of all bookings
     */
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    /**
     * Get booking by id
     * @param id Booking id
     * @return Booking if found, empty Optional otherwise
     */
    public Optional<Booking> getBookingById(String id) {
        return bookingRepository.findById(id);
    }

    /**
     * Get all bookings for a specific showtime
     * @param showtimeId Showtime id
     * @return List of bookings for the showtime
     */
    public List<Booking> getBookingsByShowtimeId(Long showtimeId) {
        return bookingRepository.findByShowtimeId(showtimeId);
    }

    /**
     * Create a new booking
     * @param booking Booking to create
     * @return Created booking with generated id
     * @throws IllegalArgumentException if validation fails
     */
    public Booking createBooking(Booking booking) {
        // Validate showtime exists
        if (!showtimeRepository.existsById(booking.getShowtimeId())) {
            throw new IllegalArgumentException("Showtime with ID " + booking.getShowtimeId() + " does not exist");
        }

        // Check if seat is already booked
        if (bookingRepository.existsByShowtimeIdAndSeatNumber(booking.getShowtimeId(), booking.getSeatNumber())) {
            throw new IllegalArgumentException("Seat " + booking.getSeatNumber() + " is already booked for this showtime");
        }

        // Generate UUID for booking id if not provided
        if (booking.getId() == null || booking.getId().isEmpty()) {
            booking.setId(UUID.randomUUID().toString());
        }

        return bookingRepository.save(booking);
    }

    /**
     * Delete a booking by id
     * @param id Booking id to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteBooking(String id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        if (booking.isPresent()) {
            bookingRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

