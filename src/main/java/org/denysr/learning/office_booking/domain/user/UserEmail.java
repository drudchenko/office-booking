package org.denysr.learning.office_booking.domain.user;

import lombok.Value;
import org.apache.commons.lang3.Validate;
import org.apache.commons.validator.routines.EmailValidator;
import org.denysr.learning.office_booking.domain.validation.ValidatorWrapper;

@Value
public class UserEmail {
    private final static int MIN_EMAIL_LENGTH = 6;
    private final static int MAX_EMAIL_LENGTH = 100;
    private final static String WRONG_EMAIL_LENGTH_ERROR_MESSAGE = "email Length should be between " +
            MIN_EMAIL_LENGTH + " and " + MAX_EMAIL_LENGTH;
    private final static EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance(false);

    String email;

    public UserEmail(String email) {
        ValidatorWrapper.wrapValidators(
                () -> Validate.notBlank(email, "Email cannot be empty"),
                () -> Validate.inclusiveBetween(
                        MIN_EMAIL_LENGTH, MAX_EMAIL_LENGTH, email.length(),
                        WRONG_EMAIL_LENGTH_ERROR_MESSAGE
                ),
                () -> Validate.isTrue(EMAIL_VALIDATOR.isValid(email), "Invalid email provided")
        );
        this.email = email;
    }
}
