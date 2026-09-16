package com.theatre.Booking_System.service;

import com.theatre.Booking_System.model.Seat;
import com.theatre.Booking_System.repo.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    public List<Seat> getSeatsByScreen(Long screenId) {
        return seatRepository.findByScreenId(screenId);
    }

    public Seat createSeat(Seat seat) {
        return seatRepository.save(seat);
    }
}