package org.denysr.learning.office_booking.e2e.harness;

import java.io.UncheckedIOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Raw HTTP response, kept unparsed so that error paths can be asserted as easily as happy ones. */
public record ApiResponse(int status, String body) {
    private static final ObjectMapper JSON = new ObjectMapper();

    public JsonNode json() {
        try {
            return JSON.readTree(body);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException("Response body is not valid JSON: " + body, e);
        }
    }

    /** Id returned by the user create and change endpoints. */
    public int userId() {
        return json().path("userId").asInt();
    }

    /** Id returned by the booking create endpoint. */
    public int bookingId() {
        return json().path("bookingId").asInt();
    }

    /** Message of an {@code ErrorResponse}. */
    public String error() {
        return json().path("error").asText();
    }

    @Override
    public String toString() {
        return status + " " + body;
    }
}
