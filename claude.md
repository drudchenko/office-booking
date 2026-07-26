# Office Booking Application - Project Context

## Overview
A Spring Boot REST API application for booking office spots with user management. Built as a learning/test project.

## Tech Stack
- **Framework**: Spring Boot 4.1.0
- **Java Version**: Java 25
- **Build Tool**: Gradle 9
- **Database**: H2 (in-memory)
- **ORM**: Spring Data JPA
- **Validation**: Hibernate Validator, CommonsValidator
- **Documentation**: Springdoc OpenAPI UI (Swagger)
- **Mapping**: ModelMapper

## Architecture Layers

### Domain Layer (`/domain`)
- **Entities**: `User`, `Booking` with value objects (`UserId`, `UserEmail`, `UserName`, `BookingId`, `BookingDateRange`, `BusinessWeek`, `DateRange`)
- **Services**: `UserManagement`, `BookingManagement`
- **Repositories (interfaces)**: `UserRepository`, `BookingRepository`
- **Exceptions**: 
  - `EntityNotFoundException`
  - `IllegalValueException`
  - Domain exceptions: `UserAlreadyRegisteredException`, `UserIdMismatchException`, `UserIdMissingException`

### Infrastructure Layer (`/infrastructure`)

#### JPA (`/infrastructure/jpa`)
- Implements repositories using JPA
- Uses DTO classes for entity-mapper pattern:
  - `UserJpaDto`, `BookingJpaDto` (entity wrappers)
  - `UserRepositoryJpaImpl`, `BookingRepositoryJpaImpl` (repository implementations)

#### REST (`/infrastructure/rest`)
- API response entities:
  - `OfficeBookingRestDto` (main booking response with availability info)
  - `UserResponseEntity`
  - `BookingResponseEntity`
  - `UserRestDto`
- `ErrorResponse` for error responses

### Controllers (`/controllers`)
- `OfficeBookingController` - Booking operations
- `UserController` - User management operations

## Key Features
- User registration and management
- Office spot booking with availability checking
- Business week handling
- Date range calculations
- Comprehensive validation (domain + hibernate)
- DTO mapping between layers
- Swagger API documentation at `/swagger-ui.html`

## Run Commands
```bash
./gradlew bootRun           # Start application
./gradlew test              # Run tests
./gradlew clean build       # Build with tests
```

## API Endpoints (Swagger UI: http://localhost:8080/swagger-ui.html)
- User endpoints via `/users`
- Office booking endpoints via `/office-bookings`

## Project Structure
```
src/main/java/org/denysr/learning/office_booking/
├── config/              # ModelMapperConfig, etc.
├── controllers/         # REST controller classes
├── domain/              # Domain entities and services
│   ├── booking/
│   ├── user/
│   ├── validation/
│   └── date/
├── infrastructure/
│   ├── jpa/             # JPA repositories and DTOs
│   └── rest/            # REST DTOs and response entities
```

## Notes
- Project group: `org.denysr.learning-projects`
- Version: 1.0.0-SNAPSHOT
- Uses entity-mapper pattern with DTOs for layer separation