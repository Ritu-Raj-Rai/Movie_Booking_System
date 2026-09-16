package com.theatre.Booking_System.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowResponseDTO {
    private Long id;
    private String movieTitle;
    private String theatreName;
    private int screenNumber;
    private LocalDateTime showTime;
    private BigDecimal price;
}