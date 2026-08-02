package org.denysr.learning.office_booking.domain.user.exceptions;

/** Thrown when a user is stored under an email address another user already uses. */
public class EmailAlreadyTakenException extends RuntimeException {
    public EmailAlreadyTakenException(String message) {
        super(message);
    }

    public EmailAlreadyTakenException(String message, Throwable cause) {
        super(message, cause);
    }
}
