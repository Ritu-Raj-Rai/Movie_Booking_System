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

    public Screen updateScreen(Long id, Screen screenDetails) {
        Screen existing = screenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Screen not found with id: " + id));

        if (screenDetails.getScreenNumber() != null) existing.setScreenNumber(screenDetails.getScreenNumber());
        if (screenDetails.getTotalSeats() != null) existing.setTotalSeats(screenDetails.getTotalSeats());

        return screenRepository.save(existing);
    }

    public void deleteScreen(Long id) {
        if (!screenRepository.existsById(id)) {
            throw new RuntimeException("Screen not found with id: " + id);
        }
        screenRepository.deleteById(id);
    }
}