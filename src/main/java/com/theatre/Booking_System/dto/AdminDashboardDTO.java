package com.theatre.Booking_System.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardDTO {
    private long totalMovies;
    private long totalTheatres;
    private long totalScreens;
    private long totalShows;
    private long totalBookings;
    private long totalUsers;
    private BigDecimal totalRevenue;
}
