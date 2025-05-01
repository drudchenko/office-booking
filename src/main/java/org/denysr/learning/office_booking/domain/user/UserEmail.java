package org.denysr.learning.office_booking.domain.user;

import org.apache.commons.lang3.Validate;
import org.apache.commons.validator.routines.EmailValidator;
import org.denysr.learning.office_booking.domain.validation.ValidatorWrapper;

public record UserEmail(String email) {
    private final static int MIN_EMAIL_LENGTH = 6;
    private final static int MAX_EMAIL_LENGTH = 100;
    private final static String WRONG_EMAIL_LENGTH_ERROR_MESSAGE = "email Length should be between " +
            MIN_EMAIL_LENGTH + " and " + MAX_EMAIL_LENGTH;
    private final static EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance(false);

    public UserEmail(final String email) {
        ValidatorWrapper.wrapValidators(
                () -> Validate.notBlank(email, "Email cannot be empty")
        );
        final String emailTrimmed = email.trim();
        ValidatorWrapper.wrapValidators(
                () -> Validate.inclusiveBetween(
                        MIN_EMAIL_LENGTH, MAX_EMAIL_LENGTH, emailTrimmed.length(),
                        WRONG_EMAIL_LENGTH_ERROR_MESSAGE
                ),
                () -> Validate.isTrue(EMAIL_VALIDATOR.isValid(emailTrimmed), "Invalid email provided")
        );
        this.email = emailTrimmed;
    }
}
