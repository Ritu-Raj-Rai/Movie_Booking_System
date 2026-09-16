package com.theatre.Booking_System.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentWebhookRequestDTO {
    private String paymentId;
    private String status; // "SUCCESS", "FAILED"
}
