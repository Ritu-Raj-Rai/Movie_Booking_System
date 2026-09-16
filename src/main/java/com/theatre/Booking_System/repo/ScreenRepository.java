package com.theatre.Booking_System.repo;

import com.theatre.Booking_System.model.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScreenRepository extends JpaRepository<Screen, Long> {

    List<Screen> findByTheatreId(Long theatreId);
}