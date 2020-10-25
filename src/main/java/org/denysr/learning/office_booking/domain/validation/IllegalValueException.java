package org.denysr.learning.office_booking.domain.validation;

public class IllegalValueException extends RuntimeException {
    public IllegalValueException(String message) {
        super(message);
    }
}
