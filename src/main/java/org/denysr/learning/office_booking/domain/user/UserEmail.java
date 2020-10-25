package org.denysr.learning.office_booking.domain.user;

import lombok.Value;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.util.Assert;

@Value
public class UserEmail {
    private final static int MIN_EMAIL_LENGTH = 6;
    private final static int MAX_EMAIL_LENGTH = 100;
    private final static EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance(false);

    String email;

    public UserEmail(String email) {
        Assert.notNull(email, "Email cannot be empty");
        int emailLength = email.length();
        Assert.isTrue(
                emailLength > MIN_EMAIL_LENGTH && emailLength < MAX_EMAIL_LENGTH,
                "email Length should be between " + MIN_EMAIL_LENGTH + " and " + MAX_EMAIL_LENGTH
        );
        Assert.isTrue(EMAIL_VALIDATOR.isValid(email), "Invalid email provided");
        this.email = email;
    }
}
