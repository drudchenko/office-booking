package org.denysr.learning.office_booking.domain.user;

import org.apache.commons.lang3.Validate;
import org.denysr.learning.office_booking.domain.validation.ValidatorWrapper;

public record UserId(int userId) implements Comparable<UserId> {
    public UserId {
        ValidatorWrapper.wrapValidators(
                () -> Validate.isTrue(userId > 0, "User id should be above 0.")
        );
    }

    @Override
    public int compareTo(UserId o) {
        return Integer.compare(userId(), o.userId());
    }
}
