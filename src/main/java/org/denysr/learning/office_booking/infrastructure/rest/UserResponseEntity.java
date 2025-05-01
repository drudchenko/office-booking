package org.denysr.learning.office_booking.infrastructure.rest;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "User response")
public record UserResponseEntity (

    @Schema(description = "User id", example = "3")
    int userId,

    @Schema(description = "User email", example = "john.doe@example.com")
    String email,

    @Schema(description = "User first name", example = "John")
    String firstName,

    @Schema(description = "User second name", example = "Doe")
    String secondName
) {
}