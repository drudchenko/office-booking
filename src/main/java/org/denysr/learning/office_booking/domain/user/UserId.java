package org.denysr.learning.office_booking.domain.user;

import lombok.Data;
import org.springframework.util.Assert;

@Data
public class UserId implements Comparable<UserId> {
    private final int userId;

    public UserId(int userId) {
        Assert.isTrue(userId > 0, "User id should be above 0.");
        this.userId = userId;
    }

    @Override
    public int compareTo(UserId o) {
        return Integer.compare(getUserId(), o.getUserId());
    }
}
