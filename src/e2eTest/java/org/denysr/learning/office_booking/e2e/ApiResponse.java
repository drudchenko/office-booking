package org.denysr.learning.office_booking.e2e;

import java.io.UncheckedIOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Raw HTTP response, kept unparsed so that error paths can be asserted as easily as happy ones. */
record ApiResponse(int status, String body) {
    private static final ObjectMapper JSON = new ObjectMapper();

    JsonNode json() {
        try {
            return JSON.readTree(body);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException("Response body is not valid JSON: " + body, e);
        }
    }

    /** Id returned by the create and change endpoints. */
    int userId() {
        return json().path("userId").asInt();
    }

    /** Message of an {@code ErrorResponse}. */
    String error() {
        return json().path("error").asText();
    }

    @Override
    public String toString() {
        return status + " " + body;
    }
}
