package org.denysr.learning.office_booking.infrastructure.rest;

import lombok.Data;
import org.denysr.learning.office_booking.domain.user.UserName;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public final class UserRestDto {

    @Email
    @NotBlank
    private final String email;

    @Size(min = UserName.MIN_USER_NAME_LENGTH, max = UserName.MAX_USER_NAME_LENGTH)
    @NotBlank
    private final String firstName;

    @Size(min = UserName.MIN_USER_NAME_LENGTH, max = UserName.MAX_USER_NAME_LENGTH)
    @NotBlank
    private final String secondName;
}
