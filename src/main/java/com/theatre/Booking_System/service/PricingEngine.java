package com.theatre.Booking_System.service;

import com.theatre.Booking_System.model.Seat;
import com.theatre.Booking_System.model.Show;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class PricingEngine {

    @Data
    @Builder
    public static class PricingResult {
        private BigDecimal baseAmount;
        private BigDecimal surgeAmount;
        private BigDecimal discountAmount;
        private BigDecimal totalAmount;
        private String promoCode;
    }

    public PricingResult calculatePricing(Show show, List<Seat> requestedSeats, String promoCode) {
        // 1. Calculate Base Amount
        BigDecimal baseAmount = BigDecimal.ZERO;
        for (Seat seat : requestedSeats) {
            BigDecimal seatPrice = show.getPrice();
            if (seat.getSeatType() == Seat.SeatType.PREMIUM) {
                seatPrice = seatPrice.multiply(BigDecimal.valueOf(1.5));
            }
            baseAmount = baseAmount.add(seatPrice);
        }

        // 2. Calculate Surge Pricing Multiplier
        BigDecimal surgeMultiplier = BigDecimal.ONE;
        LocalDateTime showTime = show.getShowTime();

        // Weekend surge (+25%)
        DayOfWeek day = showTime.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            surgeMultiplier = surgeMultiplier.add(BigDecimal.valueOf(0.25));
        }

        // Evening Peak Hours surge 6:00 PM - 10:00 PM (+20%)
        int hour = showTime.getHour();
        if (hour >= 18 && hour <= 22) {
            surgeMultiplier = surgeMultiplier.add(BigDecimal.valueOf(0.20));
        }

        BigDecimal subtotal = baseAmount.multiply(surgeMultiplier).setScale(2, RoundingMode.HALF_UP);
        BigDecimal surgeAmount = subtotal.subtract(baseAmount);

        // 3. Calculate Promo Code Discount
        BigDecimal discountAmount = BigDecimal.ZERO;
        String appliedPromo = null;

        if (promoCode != null && !promoCode.trim().isEmpty()) {
            String code = promoCode.trim().toUpperCase();
            switch (code) {
                case "FIRST50":
                    // 50% discount up to max ₹100
                    BigDecimal half = subtotal.multiply(BigDecimal.valueOf(0.50));
                    discountAmount = half.min(BigDecimal.valueOf(100.00));
                    appliedPromo = "FIRST50";
                    break;

                case "WEEKEND20":
                    // 20% flat discount
                    discountAmount = subtotal.multiply(BigDecimal.valueOf(0.20));
                    appliedPromo = "WEEKEND20";
                    break;

                case "MOVIE100":
                    // Flat ₹100 off
                    discountAmount = BigDecimal.valueOf(100.00).min(subtotal);
                    appliedPromo = "MOVIE100";
                    break;

                default:
                    throw new RuntimeException("Invalid promo code: " + promoCode);
            }
        }

        discountAmount = discountAmount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal finalTotal = subtotal.subtract(discountAmount).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

        return PricingResult.builder()
                .baseAmount(baseAmount.setScale(2, RoundingMode.HALF_UP))
                .surgeAmount(surgeAmount)
                .discountAmount(discountAmount)
                .totalAmount(finalTotal)
                .promoCode(appliedPromo)
                .build();
    }
}
