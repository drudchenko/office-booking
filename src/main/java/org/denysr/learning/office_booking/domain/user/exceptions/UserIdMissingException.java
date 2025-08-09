package org.denysr.learning.office_booking.domain.user.exceptions;

public class UserIdMissingException extends RuntimeException {
    public UserIdMissingException(String message) {
        super(message);
    }
}