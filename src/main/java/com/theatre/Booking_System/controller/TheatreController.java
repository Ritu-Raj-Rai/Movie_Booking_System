package com.theatre.Booking_System.controller;

import com.theatre.Booking_System.model.Theatre;
import com.theatre.Booking_System.service.TheatreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theatres")
@RequiredArgsConstructor
public class TheatreController {

    private final TheatreService theatreService;

    @GetMapping
    public ResponseEntity<List<Theatre>> getAllTheatres() {
        return ResponseEntity.ok(theatreService.getAllTheatres());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Theatre> getTheatreById(@PathVariable Long id) {
        return ResponseEntity.ok(theatreService.getTheatreById(id));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<Theatre>> getTheatresByCity(@PathVariable String city) {
        return ResponseEntity.ok(theatreService.getTheatresByCity(city));
    }

    @PostMapping
    public ResponseEntity<Theatre> createTheatre(@RequestBody Theatre theatre) {
        return ResponseEntity.ok(theatreService.createTheatre(theatre));
    }
}