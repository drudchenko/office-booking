package org.denysr.learning.office_booking.domain.user;

import lombok.Builder;
import org.denysr.learning.office_booking.domain.validation.ValidatorWrapper;

import java.util.Objects;

@Builder(setterPrefix = "with")
public record User (UserId userId, UserEmail userEmail, UserName userName) {

    public User {
        ValidatorWrapper.wrapValidators(
                () -> Objects.requireNonNull(userEmail),
                () -> Objects.requireNonNull(userName)
        );
    }

    public boolean hasId() {
        return !Objects.isNull(userId);
    }

    public User withUserId(UserId userId) {
        return new User(userId, userEmail, userName);
    }
}
