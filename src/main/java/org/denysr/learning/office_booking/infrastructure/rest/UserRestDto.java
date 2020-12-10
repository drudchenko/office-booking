package org.denysr.learning.office_booking.infrastructure.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.denysr.learning.office_booking.domain.user.UserName;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public final class UserRestDto {

    @Email
    @NotBlank
    @Schema(description = "Email of the user", required = true, example = "john.doe@example.com")
    private final String email;

    @Size(min = UserName.MIN_USER_NAME_LENGTH, max = UserName.MAX_USER_NAME_LENGTH)
    @NotBlank
    @Schema(description = "User first name", required = true, example = "John")
    private final String firstName;

    @Size(min = UserName.MIN_USER_NAME_LENGTH, max = UserName.MAX_USER_NAME_LENGTH)
    @NotBlank
    @Schema(description = "User last name", required = true, example = "Doe")
    private final String secondName;
}
