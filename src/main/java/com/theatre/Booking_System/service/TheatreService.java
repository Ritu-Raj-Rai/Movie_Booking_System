package com.theatre.Booking_System.service;

import com.theatre.Booking_System.model.Theatre;
import com.theatre.Booking_System.repo.TheatreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TheatreService {

    private final TheatreRepository theatreRepository;

    public List<Theatre> getAllTheatres() {
        return theatreRepository.findAll();
    }

    public Theatre getTheatreById(Long id) {
        return theatreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Theatre not found with id: " + id));
    }

    public List<Theatre> getTheatresByCity(String city) {
        return theatreRepository.findByCity(city);
    }

    public Theatre createTheatre(Theatre theatre) {
        return theatreRepository.save(theatre);
    }
}