package org.denysr.learning.office_booking.infrastructure.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public final class OfficeBookingRestDto {
    @Schema(description = "Id of the existing user", required = true, example = "3")
    private final int userId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "Start date of the booking period.", required = true, example = "2020-12-06")
    private final LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(
            description = "End date of the booking period. Should be equal or after the start date",
            required = true,
            example = "2020-12-10"
    )
    private final LocalDate endDate;
}
