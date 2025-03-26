package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.service.ShowtimeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/showtimes")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @Autowired
    public ShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    /**
     * Get all showtimes
     * @return List of all showtimes
     */
    @GetMapping
    public ResponseEntity<List<Showtime>> getAllShowtimes() {
        List<Showtime> showtimes = showtimeService.getAllShowtimes();
        return new ResponseEntity<>(showtimes, HttpStatus.OK);
    }

    /**
     * Get showtime by id
     * @param id Showtime id
     * @return Showtime if found, 404 Not Found otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<Showtime> getShowtimeById(@PathVariable Long id) {
        Optional<Showtime> showtime = showtimeService.getShowtimeById(id);
        return showtime.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Create a new showtime
     * @param showtime Showtime to create
     * @return Created showtime with generated id
     */
    @PostMapping
    public ResponseEntity<?> createShowtime(@Valid @RequestBody Showtime showtime) {
        Showtime createdShowtime = showtimeService.createShowtime(showtime);
        return new ResponseEntity<>(createdShowtime, HttpStatus.OK);
    }

    /**
     * Update an existing showtime
     * @param id Showtime id to update
     * @param showtimeDetails Updated showtime details
     * @return Updated showtime if found, 404 Not Found otherwise
     */
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateShowtime(@PathVariable Long id, @Valid @RequestBody Showtime showtimeDetails) {
        Showtime updatedShowtime = showtimeService.updateShowtime(id, showtimeDetails);
        if (updatedShowtime == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedShowtime, HttpStatus.OK);
    }

    /**
     * Delete a showtime by id
     * @param id Showtime id to delete
     * @return 200 OK if deleted, 404 Not Found otherwise
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShowtime(@PathVariable Long id) {
        boolean deleted = showtimeService.deleteShowtime(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

