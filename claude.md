# Office Booking Application - Project Context

## Overview
A Spring Boot REST API application for booking office spots with user management. Built as a learning/test project.

## Tech Stack
- **Framework**: Spring Boot 4.1.0
- **Java Version**: Java 25 (Gradle toolchain)
- **Build Tool**: Gradle 9, dependency versions in the version catalog `gradle/libs.versions.toml`
- **Database**: H2 (in-memory)
- **ORM**: Spring Data JPA
- **Validation**: Hibernate Validator, CommonsValidator
- **Documentation**: Springdoc OpenAPI UI (Swagger)
- **Mapping**: ModelMapper
- **Boilerplate**: Lombok (compile-only + annotation processor)
- **Testing**: JUnit 5, Mockito (inline mock maker), AssertJ, Spring Boot test, Testcontainers (e2e)

## Architecture Layers

### Domain Layer (`/domain`)
- **Entities**: `User`, `Booking` with value objects (`UserId`, `UserEmail`, `UserName`, `BookingId`, `BookingDateRange`, `BusinessWeek`, `DateRange`)
- **Services**: `UserManagement`, `BookingManagement`
- **Repositories (interfaces)**: `UserRepository`, `BookingRepository`
- **Validation helpers** (`/domain/validation`): `ValidationProcedure`, `ValidatorWrapper`
- **Exceptions**:
  - `EntityNotFoundException`
  - `IllegalValueException`
  - Domain exceptions: `UserAlreadyRegisteredException`, `EmailAlreadyTakenException`, `UserIdMismatchException`, `UserIdMissingException`

### Infrastructure Layer (`/infrastructure`)

#### JPA (`/infrastructure/jpa`)
- Implements repositories using JPA
- Uses DTO classes for entity-mapper pattern:
  - `UserJpaDto`, `BookingJpaDto` (entity wrappers)
  - `UserRepositoryJpaImpl`, `BookingRepositoryJpaImpl` (repository implementations)
  - `JpaUserRepository`, `JpaBookingRepository` (Spring Data interfaces)

#### REST (`/infrastructure/rest`)
- API response entities:
  - `OfficeBookingRestDto` (main booking response with availability info)
  - `UserResponseEntity`
  - `BookingResponseEntity`
  - `UserRestDto`
- `ErrorResponse` for error responses

### Controllers (`/controllers`)
- `OfficeBookingController` - Booking operations, mapped under `/office`
- `UserController` - User management operations, mapped under `/users`

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
./gradlew test              # Run unit and integration tests (JVM only)
./gradlew e2eTest           # Run end-to-end tests (needs a container runtime)
./gradlew clean build       # Build with tests; does NOT run e2eTest
./gradlew bootJar           # Build the runnable jar used by the Docker image
```

## API Endpoints (Swagger UI: http://localhost:8080/swagger-ui.html)
- Users (`/users`): `POST /users/user`, `PUT /users/user/{userId}`, `GET /users/user/{userId}`,
  `GET /users/users`, `DELETE /users/user/{userId}`
- Office bookings (`/office`): `POST /office/booking`, `GET /office/bookings/{businessDay}`
  (everything booked in the business week that day falls into), `DELETE /office/booking/{bookingId}`

## Testing

### Unit and integration tests (`src/test`)
- Run in the JVM with `./gradlew test`, need nothing but the JDK.
- Domain and mapping tests are plain JUnit 5 + Mockito; the inline mock maker is enabled via
  `src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker`.
- Controller tests (`*ControllerIT`) use `@SpringBootTest` + `@AutoConfigureMockMvc` with
  `@MockitoBean` domain services, so they cover the HTTP mapping without a database.

### End-to-end tests (`src/e2eTest`)
- Live in their own Gradle source set, declared with `sourceSets { e2eTest }` in `build.gradle`.
- The source set deliberately does **not** depend on the main classes: the tests talk to the
  running application over HTTP only, so nothing but the published contract is exercised.
  Request bodies are re-declared locally as records (`UserPayload`, `BookingPayload`).
- Dependencies are `e2eTestImplementation` only (Testcontainers, JUnit Jupiter, AssertJ,
  Jackson databind); their versions come from the Spring Boot BOM.

Layout of `src/e2eTest/java/org/denysr/learning/office_booking/e2e/`:

| File | Role |
| --- | --- |
| `UserApiE2eTest` / `BookingApiE2eTest` | The tests, each `@ExtendWith(ApplicationUnderTest.class)` |
| `../resources/logback-test.xml` | Quiets Testcontainers and application container output |

The harness the tests run against lives one level down, in `.../e2e/harness/`, so the package holding
the tests only ever holds tests:

| File | Role |
| --- | --- |
| `ApplicationUnderTest` | JUnit extension: starts the app container, injects API clients |
| `ApiClient` | Marker interface; implementations take the base URL as the only ctor argument |
| `HttpCalls` | Shared `java.net.http` plumbing and JSON serialisation |
| `ApiResponse` | Raw `(status, body)` record with `json()`, `userId()`, `bookingId()`, `error()` helpers |
| `UserApiClient` / `BookingApiClient` | Clients for `/users` and `/office` |
| `UserPayload` / `BookingPayload` | Request bodies for the user and booking endpoints |

Everything the tests reference (`ApplicationUnderTest`, the API clients, `ApiResponse`, the payload
records) is `public` because it now lives in a separate package; `ApiClient` and `HttpCalls` stay
package-private since they are only used inside `harness` itself.

How it runs:
- `ApplicationUnderTest` keeps the container in the JUnit **launcher session store**, so it is
  started once per test run no matter how many classes ask for it, and stopped when the session ends.
- Readiness is a `Wait.forHttp("/users/users")` check for a 200, which only answers once the whole
  context including JPA is up (5 minute startup timeout).
- The container port is published to a random free host port, so parallel runs do not collide.
- By default the image is built from the project `Dockerfile` (`ImageFromDockerfile`, tagged
  `office-booking-e2e:latest`), so a bare `./gradlew e2eTest` works with nothing prepared.
  Pass `-Pe2eImage=<image>` to test an image built beforehand - that is what CI does.
  The `e2eTest` task passes `e2e.projectRoot` and, when given, `e2e.image` as system properties.
- The task sets `outputs.upToDateWhen { false }` (Gradle cannot know whether a container result
  still holds) and `shouldRunAfter test`.

Test conventions:
- All tests share one application instance and therefore one database. Nothing may rely on a known
  database state: user tests register their own user under a unique email, booking tests claim a
  business week of their own (weeks counted off from Monday 2030-01-07).
- Adding coverage for another part of the API means writing its client and adding one entry to the
  `ApplicationUnderTest.CLIENTS` map; the extension needs no other change.

Running e2e tests with Podman (Testcontainers speaks the Docker API):
```bash
systemctl --user start podman.socket
export DOCKER_HOST=unix:///run/user/$(id -u)/podman/podman.sock
export TESTCONTAINERS_RYUK_DISABLED=true
./gradlew e2eTest
```

## Docker
- Multi-stage `Dockerfile`: `eclipse-temurin:25-jdk` builds the jar with `./gradlew bootJar`
  (dependencies resolved in their own layer first), `eclipse-temurin:25-jre` runs it.
- Exposes port 8080; entrypoint `java -jar app.jar`.
- `docker-compose.yml` is available for running the application locally.

## CI (`.github/workflows`)
- `build-pr.yml` - on PRs to `master`: JDK 25, `./gradlew build` then `./gradlew test`.
- `e2e.yml` - on PRs and pushes to `master`: builds the image with Buildx (GitHub Actions layer
  cache) as `office-booking:e2e`, then runs `./gradlew e2eTest -Pe2eImage=office-booking:e2e`
  and uploads `build/reports/tests/e2eTest` as an artifact.

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
src/test/java/...        # Unit and integration tests (./gradlew test)
src/e2eTest/java/...     # End-to-end tests over HTTP (./gradlew e2eTest)
```

## Notes
- Project group: `org.denysr.learning-projects`
- Version: 1.0.0-SNAPSHOT
- Uses entity-mapper pattern with DTOs for layer separation
