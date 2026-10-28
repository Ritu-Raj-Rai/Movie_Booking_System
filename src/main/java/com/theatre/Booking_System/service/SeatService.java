package com.theatre.Booking_System.service;

import com.theatre.Booking_System.dto.BulkSeatCreateDTO;
import com.theatre.Booking_System.model.Screen;
import com.theatre.Booking_System.model.Seat;
import com.theatre.Booking_System.repo.ScreenRepository;
import com.theatre.Booking_System.repo.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final ScreenRepository screenRepository;

    public List<Seat> getSeatsByScreen(Long screenId) {
        return seatRepository.findByScreenId(screenId);
    }

    public Seat createSeat(Seat seat) {
        return seatRepository.save(seat);
    }

    public List<Seat> createBulkSeats(BulkSeatCreateDTO request) {
        Screen screen = screenRepository.findById(request.getScreenId())
                .orElseThrow(() -> new RuntimeException("Screen not found with id: " + request.getScreenId()));

        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= request.getSeatCount(); i++) {
            Seat seat = Seat.builder()
                    .screen(screen)
                    .seatNumber(request.getRowPrefix() + i)
                    .seatType(request.getSeatType() != null ? request.getSeatType() : Seat.SeatType.REGULAR)
                    .build();
            seats.add(seat);
        }
        return seatRepository.saveAll(seats);
    }

    public void deleteSeat(Long id) {
        if (!seatRepository.existsById(id)) {
            throw new RuntimeException("Seat not found with id: " + id);
        }
        seatRepository.deleteById(id);
    }
}