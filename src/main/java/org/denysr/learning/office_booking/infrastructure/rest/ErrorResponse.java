package org.denysr.learning.office_booking.infrastructure.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "Error")
final public class ErrorResponse {
    @Schema(description = "Error message", example = "Cannot find user with mentioned id")
    private final String error;
}
