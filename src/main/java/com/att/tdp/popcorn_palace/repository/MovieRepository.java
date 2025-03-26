package com.att.tdp.popcorn_palace.repository;

import com.att.tdp.popcorn_palace.model.Movie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    // Spring Data JPA will automatically implement basic CRUD operations
    // Custom queries can be added here if needed

    /**
     * Find movie by title
     * @param title Movie title
     * @return Optional of Movie if found, empty Optional otherwise
     */
    Optional<Movie> findByTitle(String title);
}
