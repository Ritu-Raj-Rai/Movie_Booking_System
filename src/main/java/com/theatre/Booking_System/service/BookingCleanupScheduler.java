package com.theatre.Booking_System.service;

import com.theatre.Booking_System.model.Booking;
import com.theatre.Booking_System.model.Booking.BookingStatus;
import com.theatre.Booking_System.repo.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingCleanupScheduler {

    private final BookingRepository bookingRepository;
    private final SeatHoldService seatHoldService;

    @Scheduled(cron = "0 * * * * *") // Runs every minute
    @Transactional
    public void cleanupExpiredBookingsAndHolds() {
        // 1. Clean expired in-memory seat holds
        seatHoldService.cleanExpiredHolds();

        // 2. Find PENDING_PAYMENT bookings that have expired
        LocalDateTime now = LocalDateTime.now();
        List<Booking> expiredBookings = bookingRepository.findByStatusAndExpiresAtBefore(
                BookingStatus.PENDING_PAYMENT,
                now
        );

        for (Booking booking : expiredBookings) {
            booking.setStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);

            List<Long> seatIds = booking.getBookingSeats().stream()
                    .map(bs -> bs.getSeat().getId())
                    .collect(Collectors.toList());

            seatHoldService.releaseHold(booking.getShow().getId(), seatIds);
        }
    }
}
