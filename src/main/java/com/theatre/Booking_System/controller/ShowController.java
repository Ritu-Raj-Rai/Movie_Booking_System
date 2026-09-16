package com.theatre.Booking_System.controller;

import com.theatre.Booking_System.dto.ShowResponseDTO;
import com.theatre.Booking_System.model.Show;
import com.theatre.Booking_System.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ShowResponseDTO>> getShowsByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(showService.getShowsByMovie(movieId));
    }

    @PostMapping
    public ResponseEntity<Show> createShow(@RequestBody Show show) {
        return ResponseEntity.ok(showService.createShow(show));
    }
}