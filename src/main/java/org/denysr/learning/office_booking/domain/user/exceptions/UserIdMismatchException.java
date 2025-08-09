package org.denysr.learning.office_booking.domain.user.exceptions;

public class UserIdMismatchException extends RuntimeException {
    public UserIdMismatchException(String message) {
        super(message);
    }
}