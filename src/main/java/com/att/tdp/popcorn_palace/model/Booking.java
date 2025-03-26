package com.att.tdp.popcorn_palace.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.Instant;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @Column(length = 36)
    private String id;

    @NotNull(message = "Showtime ID is required")
    @Column(name = "showtime_id", nullable = false)
    private Long showtimeId;

    @NotBlank(message = "User ID is required")
    @Size(max = 36, message = "User ID cannot exceed 36 characters")
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @NotNull(message = "Seat number is required")
    @Min(value = 1, message = "Seat number must be at least 1")
    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Column(name = "booking_time")
    private Instant bookingTime;

    // Default constructor
    public Booking() {
        this.bookingTime = Instant.now();
    }

    // Constructor with all fields
    public Booking(String id, Long showtimeId, String userId, Integer seatNumber) {
        this.id = id;
        this.showtimeId = showtimeId;
        this.userId = userId;
        this.seatNumber = seatNumber;
        this.bookingTime = Instant.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(Long showtimeId) {
        this.showtimeId = showtimeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Instant getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(Instant bookingTime) {
        this.bookingTime = bookingTime;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id='" + id + '\'' +
                ", showtimeId=" + showtimeId +
                ", userId='" + userId + '\'' +
                ", seatNumber=" + seatNumber +
                ", bookingTime=" + bookingTime +
                '}';
    }
}

