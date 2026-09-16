# 🎬 Movie & Theatre Booking System Backend

[![Java](https://img.shields.io/badge/Java-21-orange.svg?style=flat-square&logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen.svg?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-blue.svg?style=flat-square&logo=springsecurity)](https://spring.io/projects/spring-security)
[![MySQL](https://img.shields.io/badge/Database-MySQL%20%2F%20H2-blue.svg?style=flat-square&logo=mysql)](https://www.mysql.com/)
[![Build Status](https://img.shields.io/badge/Tests-17%20Passed%20%2F%200%20Failed-success.svg?style=flat-square)](#-testing)

A high-performance, robust RESTful API backend service for a modern **Movie & Theatre Seat Booking Platform**, built using **Java 21**, **Spring Boot 3.5**, **Spring Security (JWT)**, and **Spring Data JPA**.

---

## 🚀 Key Features

- 🔐 **Authentication & Authorization**: Secure User Registration & Login with JWT token generation and Role-Based Access Control (`ROLE_USER`, `ROLE_ADMIN`).
- 🍿 **Catalog Management**: Full REST CRUD APIs for managing Movies, Theatres, Screens, Shows, and Seats (supports `REGULAR` and `PREMIUM` seat types).
- ⏱️ **Temporary Seat Hold Service**: Thread-safe in-memory seat holding mechanism to lock seats for a user during checkout and prevent concurrent double-booking.
- 💰 **Dynamic Pricing Engine**: Calculates total booking prices based on base seat price, seat type multipliers, dynamic demand-based surge pricing, discounts, and promo codes.
- 🔄 **Automated Cleanup Scheduler**: Background `@Scheduled` task that periodically sweeps expired pending bookings and releases held seats back to the pool.
- 💳 **Payment & Webhook Integration**: Complete workflow for initiating payments, handling webhook notifications, and updating booking status (`PENDING_PAYMENT` ➔ `CONFIRMED` / `EXPIRED`).

---

## 🛠️ Tech Stack & Architecture

- **Language & Runtime**: Java 21
- **Framework**: Spring Boot 3.5.3
- **Security**: Spring Security 6 + JJWT (`0.12.6`)
- **Database & ORM**: MySQL 8 (Production/Dev), H2 Database (In-Memory for Integration Testing), Spring Data JPA, Hibernate
- **Build Tool**: Maven (`mvnw` wrapper included)
- **Utilities**: Lombok, Bean Validation (`jakarta.validation`)
- **Testing**: JUnit 5, Mockito, Spring Security Test

---

## 📁 Project Structure

```text
Booking_System/
├── src/
│   ├── main/java/com/theatre/Booking_System/
│   │   ├── config/          # Security & Password Encoder configurations
│   │   ├── controller/      # REST API Controllers (Auth, Booking, Movie, Payment, etc.)
│   │   ├── dto/             # Request & Response Data Transfer Objects
│   │   ├── exception/       # Global Exception Handler & API Error payload mapping
│   │   ├── model/           # JPA Entities (User, Booking, Show, Seat, Theatre, etc.)
│   │   ├── repo/            # Spring Data JPA Repositories
│   │   ├── security/        # JWT Utils, JwtAuthenticationFilter, CustomUserDetailsService
│   │   └── service/         # Business Logic, PricingEngine, SeatHoldService, Schedulers
│   ├── main/resources/
│   │   └── application.properties   # Main Spring Boot configuration
│   └── test/                # Unit & Integration Tests (17 tests covering Service & Controllers)
├── pom.xml                  # Maven dependencies & build configuration
└── README.md                # Project documentation
```

---

## 🔌 API Endpoints Summary

### 🔑 Authentication
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/signup` | Register a new user account | Public |
| `POST` | `/api/auth/login` | Authenticate & receive JWT Token | Public |

### 🎬 Movies & Shows
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/movies` | List all available movies | Public |
| `GET` | `/api/movies/{id}` | Get movie details | Public |
| `GET` | `/api/shows` | Search shows by movie, theatre, date | Public |
| `GET` | `/api/shows/{id}/seats` | Fetch real-time seat availability for a show | Public |

### 🎟️ Bookings & Payments
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/bookings/hold` | Hold seats temporarily (10-min hold timer) | Authenticated |
| `POST` | `/api/bookings` | Create pending booking from held seats | Authenticated |
| `GET` | `/api/bookings/my-bookings` | Fetch user booking history | Authenticated |
| `POST` | `/api/payments/initiate` | Initiate payment for a pending booking | Authenticated |
| `POST` | `/api/payments/webhook` | Process payment gateway webhook callback | Public |

---

## ⚙️ Getting Started

### Prerequisites
- **JDK 21** or higher
- **Maven 3.8+** (or use included `mvnw` wrapper)
- **MySQL 8.0+** running locally on port `3306`

### Setup Database
Create a MySQL database named `movie_booking_system`:
```sql
CREATE DATABASE movie_booking_system;
```

Update your database credentials in `src/main/resources/application.properties` if needed:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/movie_booking_system
spring.datasource.username=root
spring.datasource.password=your_password
```

### Run Locally
```bash
# Clone the repository
git clone https://github.com/Ritu-Raj-Rai/Movie_Booking_System.git
cd Movie_Booking_System/Booking_System

# Run application
.\mvnw spring-boot:run
```
The server will start at `http://localhost:8080`.

---

## 🧪 Testing

Run the full automated test suite (including controller and service unit tests):

```bash
.\mvnw test
```

All 17 integration and unit tests run against an in-memory H2 database to verify seat holding, pricing rules, and booking flows.

---

## 📜 License

This project is open-source under the [MIT License](LICENSE).
