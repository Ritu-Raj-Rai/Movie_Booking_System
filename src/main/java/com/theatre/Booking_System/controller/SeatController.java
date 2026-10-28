package com.theatre.Booking_System.controller;

import com.theatre.Booking_System.dto.BulkSeatCreateDTO;
import com.theatre.Booking_System.model.Seat;
import com.theatre.Booking_System.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/screen/{screenId}")
    public ResponseEntity<List<Seat>> getSeatsByScreen(@PathVariable Long screenId) {
        return ResponseEntity.ok(seatService.getSeatsByScreen(screenId));
    }

    @PostMapping
    public ResponseEntity<Seat> createSeat(@RequestBody Seat seat) {
        return ResponseEntity.ok(seatService.createSeat(seat));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Seat>> createBulkSeats(@RequestBody BulkSeatCreateDTO request) {
        return ResponseEntity.ok(seatService.createBulkSeats(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSeat(@PathVariable Long id) {
        seatService.deleteSeat(id);
        return ResponseEntity.ok("Seat deleted successfully");
    }
}