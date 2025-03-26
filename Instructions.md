# Movie Ticket Booking System "Popcorn Palace"

This document provides instructions on how to set up, build, run, and test the Movie Ticket Booking System.

## Project Overview

Popcorn Palace is a RESTful API for a movie ticket booking system built with Spring Boot. The system manages movies, showtimes, and ticket bookings with the following features:

- Movie Management: Add, update, delete, and fetch movies
- Showtime Management: Add, update, delete, and fetch showtimes with validation for overlapping showtimes
- Ticket Booking: Book tickets for available showtimes with validation for duplicate seat bookings

## Prerequisites

1. Java SDK 17 or higher - https://www.oracle.com/java/technologies/downloads/#java21
2. Maven - https://maven.apache.org/download.cgi
3. Docker - https://www.docker.com/products/docker-desktop/
4. Git - https://git-scm.com/downloads
5. IDE (optional) - IntelliJ IDEA, Eclipse, or VS Code

## Setup and Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd popcorn-palace
```

### 2. Start the PostgreSQL Database

The project includes a `compose.yml` file to set up a PostgreSQL database using Docker:

```bash
docker-compose up -d
```

This will start a PostgreSQL instance with the following configuration:
- Host: localhost
- Port: 5432
- Database: popcorn-palace
- Username: popcorn-palace
- Password: popcorn-palace

### 3. Build the Application

```bash
./mvnw clean install
```

### 4. Run the Application

```bash
./mvnw spring-boot:run
```

The application will start on port 8080 by default. You can access it at http://localhost:8080.

## Database Schema

The application uses the following database schema:

1. **Movie Table**
   - id (Primary Key)
   - title
   - genre
   - duration
   - rating
   - release_year

2. **Showtime Table**
   - id (Primary Key)
   - movie_id (Foreign Key)
   - theater
   - start_time
   - end_time
   - price
   - Constraint: No overlapping showtimes for the same theater

3. **Booking Table**
   - id (Primary Key)
   - showtime_id (Foreign Key)
   - user_id
   - seat_number
   - booking_time
   - Constraint: No duplicate seat bookings for the same showtime

## API Documentation

### Movie APIs

| API Description | Endpoint | Request Body | Response Status | Response Body |
|-----------------|----------|--------------|-----------------|---------------|
| Get all movies | GET /movies/all | | 200 OK | Array of movie objects |
| Add a movie | POST /movies | `{ "title": "Sample Movie Title", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 }` | 200 OK | Created movie object |
| Update a movie | POST /movies/update/{movieTitle} | `{ "title": "Updated Title", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 }` | 200 OK | |
| Delete a movie | DELETE /movies/{movieTitle} | | 200 OK | |

### Showtime APIs

| API Description | Endpoint | Request Body | Response Status | Response Body |
|-----------------|----------|--------------|-----------------|---------------|
| Get showtime by ID | GET /showtimes/{showtimeId} | | 200 OK | Showtime object |
| Add a showtime | POST /showtimes | `{ "movieId": 1, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z", "price": 20.2 }` | 200 OK | Created showtime object |
| Update a showtime | POST /showtimes/update/{showtimeId} | `{ "movieId": 1, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z", "price": 50.2 }` | 200 OK | |
| Delete a showtime | DELETE /showtimes/{showtimeId} | | 200 OK | |

### Booking APIs

| API Description | Endpoint | Request Body | Response Status | Response Body |
|-----------------|----------|--------------|-----------------|---------------|
| Book a ticket | POST /bookings | `{ "showtimeId": 1, "userId": "84438967-f68f-4fa0-b620-0f08217e76af", "seatNumber": 15 }` | 200 OK | `{ "bookingId": "d1a6423b-4469-4b00-8c5f-e3cfc42eacae" }` |

## Error Handling

The API implements comprehensive error handling with informative error messages:

- Validation errors: Returns 400 Bad Request with details about the validation failure
- Resource not found: Returns 404 Not Found
- Business rule violations: Returns 400 Bad Request with a specific error message
- Server errors: Returns 500 Internal Server Error

Error responses follow this format:
```json
{
  "status": 400,
  "message": "Error message",
  "path": "/api/endpoint",
  "timestamp": "2025-03-21T10:30:00Z"
}
```

## Testing

The application includes comprehensive unit and integration tests:

### Running Tests

```bash
./mvnw test
```

### Test Coverage

- Unit tests for all service classes
- Integration tests for all controllers
- Validation of business rules (no overlapping showtimes, no duplicate seat bookings)
- Database constraints testing

## Additional Information

- The application uses Spring Data JPA for database operations
- Input validation is implemented using Jakarta Bean Validation
- Global exception handling is implemented for consistent error responses
- The database schema is automatically created on application startup
- Sample data is loaded from data.sql for testing purposes
