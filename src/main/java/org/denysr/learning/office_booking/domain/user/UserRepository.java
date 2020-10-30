package org.denysr.learning.office_booking.domain.user;

import org.denysr.learning.office_booking.domain.validation.EntityNotFoundException;

import java.util.List;

public interface UserRepository {
    User findUserById(UserId userId) throws EntityNotFoundException;
    List<User> getAllUsers();
    UserId saveUser(User user) throws EntityNotFoundException;
    void deleteUser(UserId userId) throws EntityNotFoundException;
}
