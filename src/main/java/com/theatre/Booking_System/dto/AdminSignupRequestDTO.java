package com.theatre.Booking_System.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminSignupRequestDTO {
    private String name;
    private String email;
    private String password;
    private String adminSecretKey;
}
