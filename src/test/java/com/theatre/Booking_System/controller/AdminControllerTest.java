package com.theatre.Booking_System.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theatre.Booking_System.dto.AdminSignupRequestDTO;
import com.theatre.Booking_System.dto.UserSignupRequestDTO;
import com.theatre.Booking_System.dto.auth.AuthRequestDTO;
import com.theatre.Booking_System.model.Movie;
import com.theatre.Booking_System.model.Theatre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Integration Test: Admin Default Login, Register Admin, RBAC Authorization & CRUD")
    void testAdminSecurityAndOperations() throws Exception {
        // 1. Default Admin initialized on startup can log in
        AuthRequestDTO defaultAdminLogin = new AuthRequestDTO("admin@booking.com", "Admin@password123");
        MvcResult adminLoginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(defaultAdminLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String adminToken = objectMapper.readTree(adminLoginResult.getResponse().getContentAsString()).get("token").asText();

        // 2. Register standard user & obtain token
        UserSignupRequestDTO userSignup = new UserSignupRequestDTO("Normal User", "user@example.com", "user123");
        MvcResult userSignupResult = mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userSignup)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("USER"))
                .andReturn();

        String userToken = objectMapper.readTree(userSignupResult.getResponse().getContentAsString()).get("token").asText();

        // 3. Regular user trying to create movie should be 403 Forbidden
        Movie movieToCreate = Movie.builder()
                .title("Inception")
                .description("Sci-Fi thriller")
                .durationMinutes(148)
                .genre("Sci-Fi")
                .language("English")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .build();

        mockMvc.perform(post("/api/movies")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieToCreate)))
                .andExpect(status().isForbidden());

        // 4. Admin user creating movie should succeed
        mockMvc.perform(post("/api/movies")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Inception"));

        // 5. Admin user creating theatre should succeed
        Theatre theatre = Theatre.builder()
                .name("PVR Cinemas")
                .location("Downtown Mall")
                .city("Mumbai")
                .build();

        mockMvc.perform(post("/api/theatres")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(theatre)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("PVR Cinemas"));

        // 6. Admin accessing dashboard should succeed
        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMovies").value(1))
                .andExpect(jsonPath("$.totalTheatres").value(1));
    }
}
