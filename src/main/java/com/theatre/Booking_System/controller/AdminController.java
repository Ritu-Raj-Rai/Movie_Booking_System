package com.theatre.Booking_System.controller;

import com.theatre.Booking_System.dto.AdminDashboardDTO;
import com.theatre.Booking_System.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;
    private final ScreenRepository screenRepository;
    private final ShowRepository showRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDTO> getDashboardStats() {
        BigDecimal revenue = bookingRepository.calculateTotalRevenue();
        if (revenue == null) {
            revenue = BigDecimal.ZERO;
        }

        AdminDashboardDTO stats = AdminDashboardDTO.builder()
                .totalMovies(movieRepository.count())
                .totalTheatres(theatreRepository.count())
                .totalScreens(screenRepository.count())
                .totalShows(showRepository.count())
                .totalBookings(bookingRepository.count())
                .totalUsers(userRepository.count())
                .totalRevenue(revenue)
                .build();

        return ResponseEntity.ok(stats);
    }
}
