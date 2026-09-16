package com.theatre.Booking_System.service;

import com.theatre.Booking_System.dto.BookingRequestDTO;
import com.theatre.Booking_System.dto.BookingResponseDTO;
import com.theatre.Booking_System.dto.SeatHold;
import com.theatre.Booking_System.model.*;
import com.theatre.Booking_System.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingSeatRepository bookingSeatRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShowRepository showRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private SeatHoldService seatHoldService;

    @Mock
    private PricingEngine pricingEngine;

    @InjectMocks
    private BookingService bookingService;

    private User sampleUser;
    private Movie sampleMovie;
    private Theatre sampleTheatre;
    private Screen sampleScreen;
    private Show sampleShow;
    private Seat sampleSeat;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder().id(1L).name("John Doe").email("john@example.com").build();
        sampleMovie = Movie.builder().id(1L).title("Inception").build();
        sampleTheatre = Theatre.builder().id(1L).name("PVR Cinemas").build();
        sampleScreen = Screen.builder().id(1L).screenNumber(1).theatre(sampleTheatre).build();
        sampleShow = Show.builder().id(1L).movie(sampleMovie).screen(sampleScreen).showTime(LocalDateTime.now().plusDays(1)).price(BigDecimal.valueOf(250)).build();
        sampleSeat = Seat.builder().id(10L).seatNumber("A1").seatType(Seat.SeatType.REGULAR).screen(sampleScreen).build();
    }

    @Test
    @DisplayName("Successfully create a booking")
    void testCreateBookingSuccess() {
        BookingRequestDTO request = new BookingRequestDTO(1L, 1L, List.of(10L), "FIRST50");

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(showRepository.findById(1L)).thenReturn(Optional.of(sampleShow));
        when(seatRepository.findAllByIdInForUpdate(List.of(10L))).thenReturn(List.of(sampleSeat));

        when(bookingSeatRepository.isSeatAlreadyBooked(10L, 1L, Booking.BookingStatus.CONFIRMED)).thenReturn(false);
        when(bookingSeatRepository.isSeatAlreadyBooked(10L, 1L, Booking.BookingStatus.PENDING_PAYMENT)).thenReturn(false);

        SeatHold hold = SeatHold.builder()
                .holdId("hold-123")
                .showId(1L)
                .seatIds(List.of(10L))
                .userId(1L)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();
        when(seatHoldService.holdSeats(1L, List.of(10L), 1L)).thenReturn(hold);

        PricingEngine.PricingResult pricingResult = PricingEngine.PricingResult.builder()
                .baseAmount(BigDecimal.valueOf(250))
                .surgeAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.valueOf(100))
                .totalAmount(BigDecimal.valueOf(150))
                .promoCode("FIRST50")
                .build();
        when(pricingEngine.calculatePricing(any(), any(), any())).thenReturn(pricingResult);

        Booking savedBooking = Booking.builder()
                .id(100L)
                .user(sampleUser)
                .show(sampleShow)
                .baseAmount(BigDecimal.valueOf(250))
                .surgeAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.valueOf(100))
                .totalAmount(BigDecimal.valueOf(150))
                .promoCode("FIRST50")
                .status(Booking.BookingStatus.PENDING_PAYMENT)
                .expiresAt(hold.getExpiresAt())
                .build();
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        BookingResponseDTO response = bookingService.createBooking(request);

        assertNotNull(response);
        assertEquals(100L, response.getBookingId());
        assertEquals("Inception", response.getMovieTitle());
        assertEquals("PVR Cinemas", response.getTheatreName());
        assertEquals(new BigDecimal("150"), response.getTotalAmount());
        assertEquals("PENDING_PAYMENT", response.getStatus());

        verify(bookingSeatRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Fail booking creation when seat is already booked or reserved")
    void testCreateBookingAlreadyBookedSeat() {
        BookingRequestDTO request = new BookingRequestDTO(1L, 1L, List.of(10L), null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(showRepository.findById(1L)).thenReturn(Optional.of(sampleShow));
        when(seatRepository.findAllByIdInForUpdate(List.of(10L))).thenReturn(List.of(sampleSeat));

        when(bookingSeatRepository.isSeatAlreadyBooked(10L, 1L, Booking.BookingStatus.CONFIRMED)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                bookingService.createBooking(request)
        );

        assertTrue(exception.getMessage().contains("already booked or reserved"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Successfully cancel a booking")
    void testCancelBooking() {
        Booking booking = Booking.builder()
                .id(100L)
                .show(sampleShow)
                .status(Booking.BookingStatus.PENDING_PAYMENT)
                .bookingSeats(List.of(BookingSeat.builder().seat(sampleSeat).build()))
                .build();

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        bookingService.cancelBooking(100L);

        assertEquals(Booking.BookingStatus.CANCELLED, booking.getStatus());
        verify(bookingRepository, times(1)).save(booking);
        verify(seatHoldService, times(1)).releaseHold(1L, List.of(10L));
    }
}
