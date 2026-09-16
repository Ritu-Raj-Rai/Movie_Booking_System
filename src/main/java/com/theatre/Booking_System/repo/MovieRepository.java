package com.theatre.Booking_System.repo;

import com.theatre.Booking_System.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByGenre(String genre);
    List<Movie> findByTitleContainingIgnoreCase(String title);
}