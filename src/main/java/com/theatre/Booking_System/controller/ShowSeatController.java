package com.theatre.Booking_System.controller;

import com.theatre.Booking_System.dto.SeatAvailabilityResponse;
import com.theatre.Booking_System.service.ShowSeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
public class ShowSeatController {

    private final ShowSeatService showSeatService;

    @GetMapping("/{showId}/seats")
    public ResponseEntity<List<SeatAvailabilityResponse>> getSeatAvailability(@PathVariable Long showId) {
        return ResponseEntity.ok(showSeatService.getSeatMatrixForShow(showId));
    }
}