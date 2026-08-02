package org.denysr.learning.office_booking.e2e.harness;

import java.time.LocalDate;

/** Client for the {@code /office} API. */
public final class BookingApiClient implements ApiClient {
    private final HttpCalls http;

    public BookingApiClient(String baseUrl) {
        this.http = new HttpCalls(baseUrl);
    }

    public ApiResponse createBooking(BookingPayload booking) {
        return http.send(http.request("/office/booking").POST(HttpCalls.jsonBody(booking)));
    }

    /** Bookings of the whole business week that the given day falls into. */
    public ApiResponse getBookingsForWeekOf(LocalDate businessDay) {
        return http.send(http.request("/office/bookings/" + businessDay).GET());
    }

    public ApiResponse deleteBooking(int bookingId) {
        return http.send(http.request("/office/booking/" + bookingId).DELETE());
    }
}
