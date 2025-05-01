package org.denysr.learning.office_booking.infrastructure.rest;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "Office booking response")
public record BookingResponseEntity(

    @Schema(description = "Booking id", example = "2")
    int bookingId,

    @Schema(description = "Full user name", example = "John Doe")
    String userName,

    @Schema(description = "Beginning of the office booking within the week", example = "2020-12-09")
    LocalDate startDate,

    @Schema(
            description = "End of the office booking within the week. Will be after the start date(or equal to it)",
            example = "2020-12-11"
    )
    LocalDate endDate
) {
    
}
