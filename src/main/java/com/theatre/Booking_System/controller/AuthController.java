package com.theatre.Booking_System.controller;

import com.theatre.Booking_System.dto.UserSignupRequestDTO;
import com.theatre.Booking_System.dto.auth.AuthRequestDTO;
import com.theatre.Booking_System.dto.auth.AuthResponseDTO;
import com.theatre.Booking_System.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDTO> signup(@RequestBody UserSignupRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody UserSignupRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
