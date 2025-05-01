package org.denysr.learning.office_booking.infrastructure.rest;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Error")
public record ErrorResponse (
    @Schema(description = "Error message", example = "Cannot find user with mentioned id")
    String error
) {
}
