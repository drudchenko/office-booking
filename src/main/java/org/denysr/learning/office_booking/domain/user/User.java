package org.denysr.learning.office_booking.domain.user;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.Validate;
import org.denysr.learning.office_booking.domain.validation.ValidatorWrapper;

import java.util.Objects;

@Data
@Builder(setterPrefix = "with")
final public class User {
    private final UserId userId;
    private final UserEmail userEmail;
    private final UserName userName;

    public User(UserId userId, UserEmail userEmail, UserName userName) {
        ValidatorWrapper.wrapValidators(
                () -> Validate.notNull(userEmail),
                () -> Validate.notNull(userName)
        );
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
    }

    public boolean hasId() {
        return !Objects.isNull(userId);
    }

    public User withUserId(UserId userId) {
        return new User(userId, userEmail, userName);
    }
}
