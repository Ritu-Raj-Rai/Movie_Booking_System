package com.theatre.Booking_System.repo;

import com.theatre.Booking_System.model.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TheatreRepository extends JpaRepository<Theatre, Long> {

    List<Theatre> findByCity(String city);
}