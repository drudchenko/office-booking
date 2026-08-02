package org.denysr.learning.office_booking.e2e;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Client for the {@code /users} API, speaking plain HTTP so that nothing but the contract is tested. */
final class UserApiClient {
    private static final ObjectMapper JSON = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final String baseUrl;

    UserApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    ApiResponse createUser(UserPayload user) {
        return send(request("/users/user").POST(jsonBody(user)));
    }

    ApiResponse changeUser(int userId, UserPayload user) {
        return send(request("/users/user/" + userId).PUT(jsonBody(user)));
    }

    ApiResponse getUser(int userId) {
        return send(request("/users/user/" + userId).GET());
    }

    ApiResponse getAllUsers() {
        return send(request("/users/users").GET());
    }

    ApiResponse deleteUser(int userId) {
        return send(request("/users/user/" + userId).DELETE());
    }

    private HttpRequest.Builder request(String path) {
        return HttpRequest.newBuilder(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
    }

    private static BodyPublisher jsonBody(Object payload) {
        try {
            return BodyPublishers.ofString(JSON.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException("Cannot serialise " + payload, e);
        }
    }

    private ApiResponse send(HttpRequest.Builder request) {
        try {
            final var response = httpClient.send(request.build(), BodyHandlers.ofString());
            return new ApiResponse(response.statusCode(), response.body());
        } catch (IOException e) {
            throw new UncheckedIOException("Request to the application under test failed", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while calling the application under test", e);
        }
    }
}
