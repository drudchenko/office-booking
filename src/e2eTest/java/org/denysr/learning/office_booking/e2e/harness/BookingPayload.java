package org.denysr.learning.office_booking.e2e.harness;

import java.time.LocalDate;

/**
 * Request body of the booking endpoint. Dates are strings so that the tests send the wire format
 * verbatim rather than whatever a date module would produce.
 */
public record BookingPayload(int userId, String startDate, String endDate) {

    public static BookingPayload of(int userId, LocalDate startDate, LocalDate endDate) {
        return new BookingPayload(userId, startDate.toString(), endDate.toString());
    }
}
