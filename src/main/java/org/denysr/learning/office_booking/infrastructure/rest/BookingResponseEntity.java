package org.denysr.learning.office_booking.infrastructure.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(name = "Office booking response")
final public class BookingResponseEntity {

    @Schema(description = "Booking id", example = "2")
    private final int bookingId;

    @Schema(description = "Full user name", example = "John Doe")
    private final String userName;

    @Schema(description = "Beginning of the office booking within the week", example = "2020-12-09")
    private final LocalDate startDate;

    @Schema(
            description = "End of the office booking within the week. Will be after the start date(or equal to it)",
            example = "2020-12-11"
    )
    private final LocalDate endDate;
}
