package org.denysr.learning.office_booking.domain.user;

import java.util.List;
import java.util.Objects;

import org.denysr.learning.office_booking.domain.user.exceptions.UserAlreadyRegisteredException;
import org.denysr.learning.office_booking.domain.user.exceptions.UserIdMismatchException;
import org.denysr.learning.office_booking.domain.user.exceptions.UserIdMissingException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
/* for now just thin layer over the repository, but in future some business logic will be placed here */
public class UserManagement {
    private final UserRepository userRepository;

    public User findUser(final UserId userId) {
        Objects.requireNonNull(userId, "UserId must not be null");
        return userRepository.findUserById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public UserId registerUser(final User user) {
        Objects.requireNonNull(user, "User must not be null");
        if (user.userId() != null) {
            throw new UserAlreadyRegisteredException("User already registered!");
        }
        return userRepository.saveUser(user);
    }

    public UserId changeUser(final User user) {
        Objects.requireNonNull(user, "User must not be null");
        if (user.userId() == null) {
            throw new UserIdMissingException("User should contain ID!");
        }
        UserId userId = userRepository.saveUser(user);
        if (!userId.equals(user.userId())) {
            throw new UserIdMismatchException("User ID mismatch! Something may have gone wrong!");
        }
        return userId;
    }

    public void unregisterUser(final UserId userId) {
        Objects.requireNonNull(userId, "UserId must not be null");
        userRepository.deleteUser(userId);
    }
}
