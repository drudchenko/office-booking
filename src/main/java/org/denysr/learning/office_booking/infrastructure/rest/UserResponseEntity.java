package org.denysr.learning.office_booking.infrastructure.rest;

import lombok.Data;

@Data
public class UserResponseEntity {
    private final int userId;
    private final String email;
    private final String firstName;
    private final String secondName;
}
