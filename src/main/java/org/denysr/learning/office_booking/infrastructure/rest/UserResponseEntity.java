package org.denysr.learning.office_booking.infrastructure.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "User response")
final public class UserResponseEntity {

    @Schema(description = "User id", example = "3")
    private final int userId;

    @Schema(description = "User email", example = "john.doe@example.com")
    private final String email;

    @Schema(description = "User first name", example = "John")
    private final String firstName;

    @Schema(description = "User second name", example = "Doe")
    private final String secondName;
}
