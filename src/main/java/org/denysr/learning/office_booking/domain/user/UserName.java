package org.denysr.learning.office_booking.domain.user;

import lombok.Value;
import org.apache.commons.lang3.Validate;
import org.denysr.learning.office_booking.domain.validation.ValidatorWrapper;

@Value
public class UserName {
    private static final int MIN_USER_NAME_LENGTH = 2;
    private static final int MAX_USER_NAME_LENGTH = 70;

    String firstName;
    String secondName;

    public UserName(final String firstName, final String secondName) {
        ValidatorWrapper.wrapValidators(
                () -> assertNameCorrect(firstName),
                () -> assertNameCorrect(secondName)
        );
        this.firstName = firstName.trim();
        this.secondName = secondName.trim();
    }

    public String getFullName() {
        return firstName + " " + secondName;
    }

    private void assertNameCorrect(final String name) {
        Validate.notBlank(name, "User name should not be empty.");
        final String nameTrimmed = name.trim();
        Validate.inclusiveBetween(
                MIN_USER_NAME_LENGTH, MAX_USER_NAME_LENGTH, nameTrimmed.length(),
                "Name length should be between " + MIN_USER_NAME_LENGTH + " and " + MAX_USER_NAME_LENGTH
        );
        Validate.matchesPattern(nameTrimmed, "^[a-zA-Z .]+$", "Name should contain only latin letters");
    }
}
