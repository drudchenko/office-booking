package org.denysr.learning.office_booking.infrastructure.rest;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public final class OfficeBookingRestDto {
    @Parameter(description = "User id", required = true, example = "3")
    private final int userId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private final LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private final LocalDate endDate;

}
