package com.theatre.Booking_System.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupRequestDTO {
    private String name;
    private String email;
    private String password;
}