package org.denysr.learning.office_booking.domain.user;

import java.util.List;

public interface UserRepository {
    User findUserById(UserId userId);
    List<User> getAllUsers();
    UserId saveUser(User user);
    void deleteUser(UserId userId);
}
