package com.theatre.Booking_System.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponseDTO {
    private Long bookingId;
    private String movieTitle;
    private String theatreName;
    private LocalDateTime showTime;
    private List<String> seatNumbers;
    private BigDecimal baseAmount;
    private BigDecimal surgeAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String promoCode;
    private String paymentId;
    private LocalDateTime expiresAt;
    private String status;
}