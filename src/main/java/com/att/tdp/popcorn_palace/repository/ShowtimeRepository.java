package com.att.tdp.popcorn_palace.repository;

import com.att.tdp.popcorn_palace.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    /**
     * Find overlapping showtimes for a specific theater
     * @param theater Theater name
     * @param startTime Start time of the showtime
     * @param endTime End time of the showtime
     * @param id Showtime ID (for update operations, to exclude current showtime)
     * @return List of overlapping showtimes
     */
    @Query("SELECT s FROM Showtime s WHERE s.theater = :theater " +
            "AND ((s.startTime <= :endTime AND s.endTime >= :startTime) " +
            "OR (s.startTime >= :startTime AND s.startTime <= :endTime) " +
            "OR (s.endTime >= :startTime AND s.endTime <= :endTime)) " +
            "AND (s.id != :id OR :id IS NULL)")
    List<Showtime> findOverlappingShowtimes(
            @Param("theater") String theater,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime,
            @Param("id") Long id);
}
