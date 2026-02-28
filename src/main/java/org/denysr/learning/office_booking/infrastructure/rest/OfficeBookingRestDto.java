package org.denysr.learning.office_booking.infrastructure.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Schema(name = "Office booking request")
public record OfficeBookingRestDto (
    @Schema(description = "Id of the existing user", requiredMode = RequiredMode.REQUIRED, example = "3")
    int userId,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "Start date of the booking period.", requiredMode = RequiredMode.REQUIRED, example = "2020-12-06")
    LocalDate startDate,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(
            description = "End date of the booking period. Should be equal or after the start date",
            requiredMode = RequiredMode.REQUIRED,
            example = "2020-12-10"
    )
    LocalDate endDate
) {
}
