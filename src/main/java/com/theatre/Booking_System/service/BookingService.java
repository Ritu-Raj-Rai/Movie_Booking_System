package com.theatre.Booking_System.service;

import com.theatre.Booking_System.dto.BookingRequestDTO;
import com.theatre.Booking_System.dto.BookingResponseDTO;
import com.theatre.Booking_System.dto.SeatHold;
import com.theatre.Booking_System.model.*;
import com.theatre.Booking_System.model.Booking.BookingStatus;
import com.theatre.Booking_System.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final UserRepository userRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final SeatHoldService seatHoldService;
    private final PricingEngine pricingEngine;

    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));

        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new RuntimeException("Show not found with ID: " + request.getShowId()));

        // 1. Pessimistic Row-Level Locking on Seats
        List<Seat> requestedSeats = seatRepository.findAllByIdInForUpdate(request.getSeatIds());

        if (requestedSeats.size() != request.getSeatIds().size()) {
            throw new RuntimeException("One or more seat IDs are invalid");
        }

        // 2. Check Database for Existing Confirmed or Active Pending Bookings
        for (Seat seat : requestedSeats) {
            boolean isConfirmed = bookingSeatRepository.isSeatAlreadyBooked(
                    seat.getId(),
                    show.getId(),
                    BookingStatus.CONFIRMED
            );

            boolean isPending = bookingSeatRepository.isSeatAlreadyBooked(
                    seat.getId(),
                    show.getId(),
                    BookingStatus.PENDING_PAYMENT
            );

            if (isConfirmed || isPending) {
                throw new RuntimeException("Seat " + seat.getSeatNumber() + " is already booked or reserved for this show");
            }
        }

        // 3. Acquire In-Memory Seat Hold (5 Minutes)
        SeatHold hold = seatHoldService.holdSeats(show.getId(), request.getSeatIds(), user.getId());

        // 4. Calculate Dynamic Pricing & Discounts
        PricingEngine.PricingResult pricing = pricingEngine.calculatePricing(
                show,
                requestedSeats,
                request.getPromoCode()
        );

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = hold.getExpiresAt();

        Booking booking = Booking.builder()
                .user(user)
                .show(show)
                .bookingTime(now)
                .expiresAt(expiresAt)
                .baseAmount(pricing.getBaseAmount())
                .surgeAmount(pricing.getSurgeAmount())
                .discountAmount(pricing.getDiscountAmount())
                .totalAmount(pricing.getTotalAmount())
                .promoCode(pricing.getPromoCode())
                .status(BookingStatus.PENDING_PAYMENT)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        List<BookingSeat> bookingSeats = requestedSeats.stream()
                .map(seat -> BookingSeat.builder()
                        .booking(savedBooking)
                        .seat(seat)
                        .build())
                .collect(Collectors.toList());

        bookingSeatRepository.saveAll(bookingSeats);

        return mapToDTO(savedBooking, requestedSeats);
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getUserBookings(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        List<Booking> bookings = bookingRepository.findByUserIdOrderByBookingTimeDesc(userId);

        return bookings.stream().map(booking -> {
            List<Seat> seats = booking.getBookingSeats().stream()
                    .map(BookingSeat::getSeat)
                    .collect(Collectors.toList());
            return mapToDTO(booking, seats);
        }).collect(Collectors.toList());
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        List<Long> seatIds = booking.getBookingSeats().stream()
                .map(bs -> bs.getSeat().getId())
                .collect(Collectors.toList());

        seatHoldService.releaseHold(booking.getShow().getId(), seatIds);
    }

    private BookingResponseDTO mapToDTO(Booking booking, List<Seat> seats) {
        return BookingResponseDTO.builder()
                .bookingId(booking.getId())
                .movieTitle(booking.getShow().getMovie().getTitle())
                .theatreName(booking.getShow().getScreen().getTheatre().getName())
                .showTime(booking.getShow().getShowTime())
                .seatNumbers(seats.stream().map(Seat::getSeatNumber).collect(Collectors.toList()))
                .baseAmount(booking.getBaseAmount())
                .surgeAmount(booking.getSurgeAmount())
                .discountAmount(booking.getDiscountAmount())
                .totalAmount(booking.getTotalAmount())
                .promoCode(booking.getPromoCode())
                .paymentId(booking.getPaymentId())
                .expiresAt(booking.getExpiresAt())
                .status(booking.getStatus().name())
                .build();
    }
}