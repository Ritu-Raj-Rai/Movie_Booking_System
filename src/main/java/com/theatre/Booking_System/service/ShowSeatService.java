package com.theatre.Booking_System.service;

import com.theatre.Booking_System.dto.SeatAvailabilityResponse;
import com.theatre.Booking_System.model.Booking.BookingStatus;
import com.theatre.Booking_System.model.Seat;
import com.theatre.Booking_System.model.Seat.SeatType;
import com.theatre.Booking_System.model.Show;
import com.theatre.Booking_System.repo.BookingSeatRepository;
import com.theatre.Booking_System.repo.SeatRepository;
import com.theatre.Booking_System.repo.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowSeatService {

    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final SeatHoldService seatHoldService;

    public List<SeatAvailabilityResponse> getSeatMatrixForShow(Long showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found with ID: " + showId));

        List<Seat> screenSeats = seatRepository.findByScreenId(show.getScreen().getId());

        Set<Long> unavailableSeatIds = new HashSet<>();
        unavailableSeatIds.addAll(bookingSeatRepository.findBookedSeatIdsByShowAndStatus(showId, BookingStatus.CONFIRMED));
        unavailableSeatIds.addAll(bookingSeatRepository.findBookedSeatIdsByShowAndStatus(showId, BookingStatus.PENDING_PAYMENT));
        unavailableSeatIds.addAll(seatHoldService.getHeldSeatIdsForShow(showId));

        return screenSeats.stream().map(seat -> {
            boolean available = !unavailableSeatIds.contains(seat.getId());

            BigDecimal seatPrice = show.getPrice();
            if (seat.getSeatType() == SeatType.PREMIUM) {
                seatPrice = seatPrice.multiply(BigDecimal.valueOf(1.5));
            }

            return SeatAvailabilityResponse.builder()
                    .seatId(seat.getId())
                    .seatNumber(seat.getSeatNumber())
                    .seatType(seat.getSeatType() != null ? seat.getSeatType().name() : null)
                    .price(seatPrice)
                    .isAvailable(available)
                    .build();
        }).collect(Collectors.toList());
    }
}