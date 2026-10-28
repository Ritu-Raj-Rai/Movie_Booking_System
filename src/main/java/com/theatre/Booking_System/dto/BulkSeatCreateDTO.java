package com.theatre.Booking_System.dto;

import com.theatre.Booking_System.model.Seat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkSeatCreateDTO {
    private Long screenId;
    private String rowPrefix; // e.g. "A", "B", "C"
    private int seatCount;    // e.g. 10 -> will generate A1 to A10
    private Seat.SeatType seatType; // REGULAR or PREMIUM
}
