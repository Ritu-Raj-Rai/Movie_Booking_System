package com.theatre.Booking_System.controller;

import com.theatre.Booking_System.dto.PaymentInitiateResponseDTO;
import com.theatre.Booking_System.dto.PaymentWebhookRequestDTO;
import com.theatre.Booking_System.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate/{bookingId}")
    public ResponseEntity<PaymentInitiateResponseDTO> initiatePayment(@PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.initiatePayment(bookingId));
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> processWebhook(@RequestBody PaymentWebhookRequestDTO webhook) {
        return ResponseEntity.ok(paymentService.processWebhook(webhook));
    }
}
