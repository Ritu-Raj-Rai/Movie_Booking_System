package com.theatre.Booking_System.service;

import com.theatre.Booking_System.model.Screen;
import com.theatre.Booking_System.repo.ScreenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScreenService {

    private final ScreenRepository screenRepository;

    public List<Screen> getScreensByTheatre(Long theatreId) {
        return screenRepository.findByTheatreId(theatreId);
    }

    public Screen getScreenById(Long id) {
        return screenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Screen not found with id: " + id));
    }

    public Screen createScreen(Screen screen) {
        return screenRepository.save(screen);
    }
}