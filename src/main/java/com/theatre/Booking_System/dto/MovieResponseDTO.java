package com.theatre.Booking_System.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieResponseDTO {
    private Long id;
    private String title;
    private String description;
    private int durationMinutes;
    private String genre;
    private String language;
    private LocalDate releaseDate;
}