package org.denysr.learning.office_booking.e2e.harness;

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

/** Plain HTTP plumbing shared by the API clients, so that nothing but the contract is tested. */
final class HttpCalls {
    private static final ObjectMapper JSON = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final String baseUrl;

    HttpCalls(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    HttpRequest.Builder request(String path) {
        return HttpRequest.newBuilder(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
    }

    static BodyPublisher jsonBody(Object payload) {
        try {
            return BodyPublishers.ofString(JSON.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException("Cannot serialise " + payload, e);
        }
    }

    ApiResponse send(HttpRequest.Builder request) {
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
