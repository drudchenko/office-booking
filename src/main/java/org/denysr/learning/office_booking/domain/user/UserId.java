package org.denysr.learning.office_booking.domain.user;

import lombok.Value;
import org.apache.commons.lang3.Validate;
import org.denysr.learning.office_booking.domain.validation.ValidatorWrapper;

@Value
public class UserId implements Comparable<UserId> {
    int userId;

    public UserId(int userId) {
        ValidatorWrapper.wrapValidators(
                () -> Validate.isTrue(userId > 0, "User id should be above 0.")
        );
        this.userId = userId;
    }

    @Override
    public int compareTo(UserId o) {
        return Integer.compare(getUserId(), o.getUserId());
    }
}
