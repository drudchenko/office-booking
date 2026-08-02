package org.denysr.learning.office_booking.e2e;

import java.time.LocalDate;

/** Client for the {@code /office} API. */
final class BookingApiClient {
    private final HttpCalls http;

    BookingApiClient(String baseUrl) {
        this.http = new HttpCalls(baseUrl);
    }

    ApiResponse createBooking(BookingPayload booking) {
        return http.send(http.request("/office/booking").POST(HttpCalls.jsonBody(booking)));
    }

    /** Bookings of the whole business week that the given day falls into. */
    ApiResponse getBookingsForWeekOf(LocalDate businessDay) {
        return http.send(http.request("/office/bookings/" + businessDay).GET());
    }

    ApiResponse deleteBooking(int bookingId) {
        return http.send(http.request("/office/booking/" + bookingId).DELETE());
    }
}
