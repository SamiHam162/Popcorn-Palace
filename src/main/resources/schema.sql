-- Drop tables if they exist to ensure clean schema
DROP TABLE IF EXISTS booking;
DROP TABLE IF EXISTS showtime;
DROP TABLE IF EXISTS movie;

-- Create movie table
CREATE TABLE IF NOT EXISTS movie (
                                     id SERIAL PRIMARY KEY,
                                     title VARCHAR(255) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    duration INTEGER NOT NULL,
    rating VARCHAR(10) NOT NULL,
    release_year INTEGER NOT NULL
    );

-- Create showtime table
CREATE TABLE IF NOT EXISTS showtime (
                                        id SERIAL PRIMARY KEY,
                                        movie_id INTEGER NOT NULL,
                                        theater VARCHAR(100) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movie(id) ON DELETE CASCADE,
    CONSTRAINT no_overlapping_showtimes UNIQUE (theater, start_time, end_time)
    );

-- Create booking table
CREATE TABLE IF NOT EXISTS booking (
    id VARCHAR(36) PRIMARY KEY,
    showtime_id INTEGER NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    seat_number INTEGER NOT NULL,
    booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (showtime_id) REFERENCES showtime(id) ON DELETE CASCADE,
    CONSTRAINT unique_seat_booking UNIQUE (showtime_id, seat_number)
    );
