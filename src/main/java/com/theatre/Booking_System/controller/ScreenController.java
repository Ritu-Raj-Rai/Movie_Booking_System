package com.theatre.Booking_System.controller;

import com.theatre.Booking_System.model.Screen;
import com.theatre.Booking_System.service.ScreenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screens")
@RequiredArgsConstructor
public class ScreenController {

    private final ScreenService screenService;

    @GetMapping("/theatre/{theatreId}")
    public ResponseEntity<List<Screen>> getScreensByTheatre(@PathVariable Long theatreId) {
        return ResponseEntity.ok(screenService.getScreensByTheatre(theatreId));
    }

    @PostMapping
    public ResponseEntity<Screen> createScreen(@RequestBody Screen screen) {
        return ResponseEntity.ok(screenService.createScreen(screen));
    }
}