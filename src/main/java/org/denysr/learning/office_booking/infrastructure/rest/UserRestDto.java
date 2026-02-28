package org.denysr.learning.office_booking.infrastructure.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import org.denysr.learning.office_booking.domain.user.UserName;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "User request")
public record UserRestDto (

    @Email
    @NotBlank
    @Schema(description = "Email of the user", requiredMode = RequiredMode.REQUIRED, example = "john.doe@example.com")
    String email,

    @Size(min = UserName.MIN_USER_NAME_LENGTH, max = UserName.MAX_USER_NAME_LENGTH)
    @NotBlank
    @Schema(description = "User first name", requiredMode = RequiredMode.REQUIRED, example = "John")
    String firstName,

    @Size(min = UserName.MIN_USER_NAME_LENGTH, max = UserName.MAX_USER_NAME_LENGTH)
    @NotBlank
    @Schema(description = "User last name", requiredMode = RequiredMode.REQUIRED, example = "Doe")
    String secondName
) {
}
