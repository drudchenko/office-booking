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

    public UserName(String firstName, String secondName) {
        ValidatorWrapper.wrapValidators(
                () -> assertNameCorrect(firstName),
                () -> assertNameCorrect(secondName)
        );
        this.firstName = firstName;
        this.secondName = secondName;
    }

    public String getFullName() {
        return firstName + " " + secondName;
    }

    private void assertNameCorrect(String name) {
        Validate.notBlank(name, "User name should not be empty.");
        Validate.inclusiveBetween(
                MIN_USER_NAME_LENGTH, MAX_USER_NAME_LENGTH, name.length(),
                "Name length should be between " + MIN_USER_NAME_LENGTH + " and " + MAX_USER_NAME_LENGTH
        );
        Validate.matchesPattern(name, "^[a-zA-Z .]+$", "Name should contain only latin letters");
    }
}
