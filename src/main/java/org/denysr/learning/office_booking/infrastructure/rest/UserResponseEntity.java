package org.denysr.learning.office_booking.infrastructure.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "User response")
final public class UserResponseEntity {

    @Schema(description = "User id", example = "3")
    private int userId;

    @Schema(description = "User email", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User first name", example = "John")
    private String firstName;

    @Schema(description = "User second name", example = "Doe")
    private String secondName;
}
