# Office Booking Application

## What
A test application that provides a REST API for booking office spots for a team. It includes basic user management functionalities.

## Why
This project was created for learning and exploration purposes.

## Technologies
- Java 25
- Spring Boot
- Gradle 9
- Springdoc OpenAPI UI

## Prerequisites
To run the application you should have:
- JDK 25

## How to run
You can run the application in one of the following ways:

1.  **Using the Gradle wrapper:**
    ```bash
    ./gradlew bootRun
    ```
2.  **Running the main application class** `OfficeBookingApplication` in your IDE.

After starting the application, you can access the API documentation via Swagger UI at the following URL:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Tests

Unit and integration tests run in the JVM and need nothing beyond the JDK:

```bash
./gradlew test
```

End-to-end tests live in `src/e2eTest` and exercise the user API over HTTP against the application
running in a container. They need a container runtime and are not part of `./gradlew build`:

```bash
./gradlew e2eTest
```

By default the image is built from the project `Dockerfile`. To test an image you already have, pass
its name:

```bash
./gradlew e2eTest -Pe2eImage=office-booking:latest
```

### Running the E2E tests with Podman

Testcontainers talks to the Docker API, so point it at the Podman socket first:

```bash
systemctl --user start podman.socket
export DOCKER_HOST=unix:///run/user/$(id -u)/podman/podman.sock
export TESTCONTAINERS_RYUK_DISABLED=true
./gradlew e2eTest
```
