# Build stage
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copy gradle files
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY gradle.properties ./

# Copy source code
COPY src src

# Build the application
RUN ./gradlew build -x test

# Run stage
FROM eclipse-temurin:25-jre
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/build/libs/office-booking-1.0.0-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
