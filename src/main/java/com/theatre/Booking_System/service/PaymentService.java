package com.theatre.Booking_System.service;

import com.theatre.Booking_System.dto.PaymentInitiateResponseDTO;
import com.theatre.Booking_System.dto.PaymentWebhookRequestDTO;
import com.theatre.Booking_System.model.Booking;
import com.theatre.Booking_System.model.Booking.BookingStatus;
import com.theatre.Booking_System.model.BookingSeat;
import com.theatre.Booking_System.repo.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final SeatHoldService seatHoldService;

    @Transactional
    public PaymentInitiateResponseDTO initiatePayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new RuntimeException("Booking ID " + bookingId + " is not in PENDING_PAYMENT state (Current state: " + booking.getStatus() + ")");
        }

        if (booking.getExpiresAt() != null && LocalDateTime.now().isAfter(booking.getExpiresAt())) {
            booking.setStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);
            throw new RuntimeException("Booking ID " + bookingId + " has expired. Please re-select seats.");
        }

        String paymentId = "PAY_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        booking.setPaymentId(paymentId);
        bookingRepository.save(booking);

        return PaymentInitiateResponseDTO.builder()
                .bookingId(booking.getId())
                .paymentId(paymentId)
                .amount(booking.getTotalAmount())
                .status("CREATED")
                .expiresAt(booking.getExpiresAt())
                .paymentGatewayUrl("http://localhost:8080/api/payments/webhook")
                .build();
    }

    @Transactional
    public String processWebhook(PaymentWebhookRequestDTO webhook) {
        Booking booking = bookingRepository.findByPaymentId(webhook.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Booking not found for Payment ID: " + webhook.getPaymentId()));

        List<Long> seatIds = booking.getBookingSeats().stream()
                .map(bs -> bs.getSeat().getId())
                .collect(Collectors.toList());

        Long showId = booking.getShow().getId();

        if ("SUCCESS".equalsIgnoreCase(webhook.getStatus())) {
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
            // Release temporary hold as booking is permanently confirmed
            seatHoldService.releaseHold(showId, seatIds);
            return "Payment successful. Booking ID " + booking.getId() + " is CONFIRMED.";
        } else {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            // Release temporary seat hold
            seatHoldService.releaseHold(showId, seatIds);
            return "Payment failed. Booking ID " + booking.getId() + " is CANCELLED.";
        }
    }
}
