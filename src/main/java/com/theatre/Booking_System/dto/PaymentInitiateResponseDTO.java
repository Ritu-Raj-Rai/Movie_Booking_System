package com.theatre.Booking_System.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInitiateResponseDTO {
    private Long bookingId;
    private String paymentId;
    private BigDecimal amount;
    private String status; // e.g. "CREATED"
    private LocalDateTime expiresAt;
    private String paymentGatewayUrl; // Mock URL for webhook simulation
}
