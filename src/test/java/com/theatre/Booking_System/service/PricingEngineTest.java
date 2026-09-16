package com.theatre.Booking_System.service;

import com.theatre.Booking_System.model.Seat;
import com.theatre.Booking_System.model.Show;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PricingEngineTest {

    private PricingEngine pricingEngine;

    @BeforeEach
    void setUp() {
        pricingEngine = new PricingEngine();
    }

    @Test
    @DisplayName("Calculate base price for Regular and Premium seats on a regular weekday morning")
    void testBasePriceCalculation() {
        // Wednesday at 10:00 AM (no weekend, no peak hour surge)
        LocalDateTime showTime = LocalDateTime.of(2026, 9, 16, 10, 0);

        Show show = Show.builder()
                .showTime(showTime)
                .price(BigDecimal.valueOf(200.00))
                .build();

        Seat regularSeat = Seat.builder()
                .seatNumber("A1")
                .seatType(Seat.SeatType.REGULAR)
                .build();

        Seat premiumSeat = Seat.builder()
                .seatNumber("P1")
                .seatType(Seat.SeatType.PREMIUM)
                .build();

        PricingEngine.PricingResult result = pricingEngine.calculatePricing(
                show,
                List.of(regularSeat, premiumSeat),
                null
        );

        // Regular = 200, Premium = 200 * 1.5 = 300. Base Total = 500.00
        assertEquals(new BigDecimal("500.00"), result.getBaseAmount());
        assertEquals(new BigDecimal("0.00"), result.getSurgeAmount());
        assertEquals(new BigDecimal("0.00"), result.getDiscountAmount());
        assertEquals(new BigDecimal("500.00"), result.getTotalAmount());
        assertNull(result.getPromoCode());
    }

    @Test
    @DisplayName("Calculate surge pricing for Weekend Evening show (+25% weekend +20% peak = +45% total surge)")
    void testWeekendAndEveningPeakSurge() {
        // Saturday at 7:00 PM (19:00) -> Weekend (+0.25) + Peak Hour (+0.20) = 1.45 multiplier
        LocalDateTime showTime = LocalDateTime.of(2026, 9, 19, 19, 0);

        Show show = Show.builder()
                .showTime(showTime)
                .price(BigDecimal.valueOf(200.00))
                .build();

        Seat regularSeat = Seat.builder()
                .seatNumber("A1")
                .seatType(Seat.SeatType.REGULAR)
                .build();

        PricingEngine.PricingResult result = pricingEngine.calculatePricing(
                show,
                List.of(regularSeat),
                null
        );

        // Base = 200.00
        // Multiplier = 1 + 0.25 + 0.20 = 1.45
        // Subtotal = 200 * 1.45 = 290.00
        // Surge Amount = 290.00 - 200.00 = 90.00
        assertEquals(new BigDecimal("200.00"), result.getBaseAmount());
        assertEquals(new BigDecimal("90.00"), result.getSurgeAmount());
        assertEquals(new BigDecimal("290.00"), result.getTotalAmount());
    }

    @Test
    @DisplayName("Apply FIRST50 promo code with max cap ₹100")
    void testPromoCodeFirst50WithCap() {
        LocalDateTime showTime = LocalDateTime.of(2026, 9, 16, 10, 0); // Wednesday morning

        Show show = Show.builder()
                .showTime(showTime)
                .price(BigDecimal.valueOf(300.00))
                .build();

        Seat regularSeat = Seat.builder()
                .seatNumber("A1")
                .seatType(Seat.SeatType.REGULAR)
                .build();

        PricingEngine.PricingResult result = pricingEngine.calculatePricing(
                show,
                List.of(regularSeat),
                "FIRST50"
        );

        // Base = 300. 50% discount = 150, but max cap is 100.
        // Discount = 100.00
        // Total = 300 - 100 = 200.00
        assertEquals(new BigDecimal("300.00"), result.getBaseAmount());
        assertEquals(new BigDecimal("100.00"), result.getDiscountAmount());
        assertEquals(new BigDecimal("200.00"), result.getTotalAmount());
        assertEquals("FIRST50", result.getPromoCode());
    }

    @Test
    @DisplayName("Apply MOVIE100 flat promo code")
    void testPromoCodeMovie100() {
        LocalDateTime showTime = LocalDateTime.of(2026, 9, 16, 10, 0);

        Show show = Show.builder()
                .showTime(showTime)
                .price(BigDecimal.valueOf(250.00))
                .build();

        Seat regularSeat = Seat.builder()
                .seatNumber("A1")
                .seatType(Seat.SeatType.REGULAR)
                .build();

        PricingEngine.PricingResult result = pricingEngine.calculatePricing(
                show,
                List.of(regularSeat),
                "MOVIE100"
        );

        // Base = 250. Flat ₹100 off -> Total = 150.00
        assertEquals(new BigDecimal("250.00"), result.getBaseAmount());
        assertEquals(new BigDecimal("100.00"), result.getDiscountAmount());
        assertEquals(new BigDecimal("150.00"), result.getTotalAmount());
        assertEquals("MOVIE100", result.getPromoCode());
    }

    @Test
    @DisplayName("Throw RuntimeException on invalid promo code")
    void testInvalidPromoCode() {
        LocalDateTime showTime = LocalDateTime.of(2026, 9, 16, 10, 0);
        Show show = Show.builder().showTime(showTime).price(BigDecimal.valueOf(100.00)).build();
        Seat seat = Seat.builder().seatNumber("A1").seatType(Seat.SeatType.REGULAR).build();

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                pricingEngine.calculatePricing(show, List.of(seat), "INVALID_CODE")
        );

        assertTrue(exception.getMessage().contains("Invalid promo code"));
    }
}
