package com.theatre.Booking_System.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {
    private Long userId;
    private Long showId;
    private List<Long> seatIds;
    private String promoCode;
}