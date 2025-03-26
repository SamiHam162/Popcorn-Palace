package com.att.tdp.popcorn_palace.repository;

import com.att.tdp.popcorn_palace.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    /**
     * Find booking by showtime ID and seat number
     * @param showtimeId Showtime ID
     * @param seatNumber Seat number
     * @return Optional of Booking if found, empty Optional otherwise
     */
    Optional<Booking> findByShowtimeIdAndSeatNumber(Long showtimeId, Integer seatNumber);

    /**
     * Find all bookings for a specific showtime
     * @param showtimeId Showtime ID
     * @return List of bookings for the showtime
     */
    List<Booking> findByShowtimeId(Long showtimeId);

    /**
     * Check if a seat is already booked for a specific showtime
     * @param showtimeId Showtime ID
     * @param seatNumber Seat number
     * @return true if seat is already booked, false otherwise
     */
    boolean existsByShowtimeIdAndSeatNumber(Long showtimeId, Integer seatNumber);
}

