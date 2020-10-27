package org.denysr.learning.office_booking.infrastructure.rest;

import lombok.Data;

import java.time.LocalDate;

@Data
final public class BookingResponseEntity {
    private final int bookingId;
    private final String userName;
    private final LocalDate startDate;
    private final LocalDate endDate;
}
