package org.denysr.learning.office_booking.domain.user;

import lombok.Value;
import org.springframework.util.Assert;

@Value
public class UserName {
    private static final int MAX_USER_NAME_LENGTH = 70;

    String firstName;
    String secondName;

    public UserName(String firstName, String secondName) {
        assertNameCorrect(firstName);
        assertNameCorrect(secondName);
        this.firstName = firstName;
        this.secondName = secondName;
    }

    public String getFullName() {
        return firstName + " " + secondName;
    }

    private void assertNameCorrect(String name) {
        Assert.hasLength(name, "User name should not be empty.");
        Assert.isTrue(name.length() <= MAX_USER_NAME_LENGTH, "Max username length is " + MAX_USER_NAME_LENGTH);
    }
}
