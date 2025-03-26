package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;

    @Autowired
    public ShowtimeService(ShowtimeRepository showtimeRepository, MovieRepository movieRepository) {
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
    }

    /**
     * Get all showtimes
     * @return List of all showtimes
     */
    public List<Showtime> getAllShowtimes() {
        return showtimeRepository.findAll();
    }

    /**
     * Get showtime by id
     * @param id Showtime id
     * @return Showtime if found, empty Optional otherwise
     */
    public Optional<Showtime> getShowtimeById(Long id) {
        return showtimeRepository.findById(id);
    }

    /**
     * Create a new showtime
     * @param showtime Showtime to create
     * @return Created showtime with generated id, or null if validation fails
     * @throws IllegalArgumentException if validation fails
     */
    public Showtime createShowtime(Showtime showtime) {
        // Validate movie exists
        if (!movieRepository.existsById(showtime.getMovieId())) {
            throw new IllegalArgumentException("Movie with ID " + showtime.getMovieId() + " does not exist");
        }

        // Validate start time is before end time
        if (showtime.getStartTime().isAfter(showtime.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        // Check for overlapping showtimes
        List<Showtime> overlappingShowtimes = showtimeRepository.findOverlappingShowtimes(
                showtime.getTheater(),
                showtime.getStartTime(),
                showtime.getEndTime(),
                null
        );

        if (!overlappingShowtimes.isEmpty()) {
            throw new IllegalArgumentException("There is already a showtime scheduled in this theater during the specified time");
        }

        return showtimeRepository.save(showtime);
    }

    /**
     * Update an existing showtime
     * @param id Showtime id to update
     * @param showtimeDetails Updated showtime details
     * @return Updated showtime if found and validation passes, null otherwise
     * @throws IllegalArgumentException if validation fails
     */
    public Showtime updateShowtime(Long id, Showtime showtimeDetails) {
        Optional<Showtime> showtime = showtimeRepository.findById(id);
        if (showtime.isPresent()) {
            // Validate movie exists
            if (!movieRepository.existsById(showtimeDetails.getMovieId())) {
                throw new IllegalArgumentException("Movie with ID " + showtimeDetails.getMovieId() + " does not exist");
            }

            // Validate start time is before end time
            if (showtimeDetails.getStartTime().isAfter(showtimeDetails.getEndTime())) {
                throw new IllegalArgumentException("Start time must be before end time");
            }

            // Check for overlapping showtimes
            List<Showtime> overlappingShowtimes = showtimeRepository.findOverlappingShowtimes(
                    showtimeDetails.getTheater(),
                    showtimeDetails.getStartTime(),
                    showtimeDetails.getEndTime(),
                    id
            );

            if (!overlappingShowtimes.isEmpty()) {
                throw new IllegalArgumentException("There is already a showtime scheduled in this theater during the specified time");
            }

            Showtime existingShowtime = showtime.get();
            existingShowtime.setMovieId(showtimeDetails.getMovieId());
            existingShowtime.setTheater(showtimeDetails.getTheater());
            existingShowtime.setStartTime(showtimeDetails.getStartTime());
            existingShowtime.setEndTime(showtimeDetails.getEndTime());
            existingShowtime.setPrice(showtimeDetails.getPrice());

            return showtimeRepository.save(existingShowtime);
        }
        return null;
    }

    /**
     * Delete a showtime by id
     * @param id Showtime id to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteShowtime(Long id) {
        Optional<Showtime> showtime = showtimeRepository.findById(id);
        if (showtime.isPresent()) {
            showtimeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
