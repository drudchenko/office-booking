package org.denysr.learning.office_booking.domain.user;

import lombok.Data;
import lombok.NonNull;

import java.util.Objects;

@Data
public class User {
    private final UserId userId;
    @NonNull
    private final UserEmail userEmail;
    @NonNull
    private final UserName userName;

    public boolean hasId() {
        return !Objects.isNull(userId);
    }

    public User withUserId(UserId userId) {
        return new User(userId, userEmail, userName);
    }
}
