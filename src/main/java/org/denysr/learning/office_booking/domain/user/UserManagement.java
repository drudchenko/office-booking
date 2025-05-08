package org.denysr.learning.office_booking.domain.user;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
/* for now just thin layer over the repository, but in future some business logic will be placed here */
public class UserManagement {
    private final UserRepository userRepository;

    public User findUser(final UserId userId) {
        return userRepository.findUserById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public UserId registerUser(final User user) {
        if (user.userId() != null) {
            throw new IllegalArgumentException("User already registered!");
        }
        return userRepository.saveUser(user);
    }

    public UserId changeUser(final User user) {
        if (user.userId() == null) {
            throw new IllegalArgumentException("User should contain ID!");
        }
        UserId userId = userRepository.saveUser(user);
        if (userId != user.userId()) {
            // Dummy check to just demonstrate the logic
            throw new IllegalArgumentException("User ID mismatch! something may have gone wrong!");
        }
        return userId;
    }

    public void unregisterUser(final UserId userId) {
        userRepository.deleteUser(userId);
    }
}
