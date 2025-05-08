package org.denysr.learning.office_booking.domain.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserManagementTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserManagement userManagement;

    @Test
    void findUser_shouldReturnUser_whenUserExists() {
        UserId userId = new UserId(123);
        User user = new User(userId, new UserEmail("john.doe@example.com"), new UserName("John", "Doe"));
        when(userRepository.findUserById(userId)).thenReturn(user);

        User result = userManagement.findUser(userId);

        assertEquals(user, result);
        verify(userRepository).findUserById(userId);
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        List<User> users = Arrays.asList(
            new User(new UserId(1), new UserEmail("alice@gmail.com"), new UserName("Alice", "Smith")),
            new User(new UserId(2), new UserEmail("bob@gmail.com"), new UserName("Bob", "Brown"))
            );
        when(userRepository.getAllUsers()).thenReturn(users);

        List<User> result = userManagement.getAllUsers();

        assertEquals(users, result);
        verify(userRepository).getAllUsers();
    }

    @Test
    void registerUser_shouldSaveUser_whenUserIsNotRegistered() {
        User user = new User(null, new UserEmail("new.user@example.com"), new UserName("New", "User"));
        UserId userId = new UserId(123);
        when(userRepository.saveUser(user)).thenReturn(userId);

        UserId result = userManagement.registerUser(user);

        assertEquals(userId, result);
        verify(userRepository).saveUser(user);
    }

    @Test
    void registerUser_shouldThrowException_whenUserIsAlreadyRegistered() {
        User user = new User(new UserId(123), new UserEmail("existing.user@example.com"), new UserName("Existing", "User"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userManagement.registerUser(user);
        });

        assertEquals("User already registered!", exception.getMessage());
        verifyNoInteractions(userRepository);
    }

    @Test
    void changeUser_shouldSaveUser_whenUserHasId() {
        User user = new User(new UserId(123), new UserEmail("updated.user@example.com"), new UserName("Updated", "User"));
        when(userRepository.saveUser(user)).thenReturn(user.userId());

        UserId result = userManagement.changeUser(user);

        assertEquals(user.userId(), result);
        verify(userRepository).saveUser(user);
    }

    @Test
    void changeUser_shouldThrowException_whenUserHasNoId() {
        User user = new User(null, new UserEmail("no@example.com"), new UserName("No ID", "User"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userManagement.changeUser(user);
        });

        assertEquals("User should contain ID!", exception.getMessage());
        verifyNoInteractions(userRepository);
    }

    @Test
    void changeUser_shouldThrowException_whenUserIdMismatch() {
        User user = new User(new UserId(123), new UserEmail("mismatched@example.com"), new UserName("Mismatched" ,"User"));
        when(userRepository.saveUser(user)).thenReturn(new UserId(456));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userManagement.changeUser(user);
        });

        assertEquals("User ID mismatch! something may have gone wrong!", exception.getMessage());
        verify(userRepository).saveUser(user);
    }

    @Test
    void unregisterUser_shouldDeleteUser() {
        UserId userId = new UserId(123);

        userManagement.unregisterUser(userId);

        verify(userRepository).deleteUser(userId);
    }
}