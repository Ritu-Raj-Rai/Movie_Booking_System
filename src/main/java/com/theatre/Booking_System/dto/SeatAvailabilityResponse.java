package com.theatre.Booking_System.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatAvailabilityResponse {
    private Long seatId;
    private String seatNumber;
    private String seatType;
    private BigDecimal price;
    private boolean isAvailable;
}