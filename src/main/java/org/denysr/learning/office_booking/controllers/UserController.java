package org.denysr.learning.office_booking.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.denysr.learning.office_booking.domain.user.*;
import org.denysr.learning.office_booking.infrastructure.rest.UserResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Log4j2
public class UserController {
    private final UserRepository userRepository;

    @PostMapping("/user")
    public ResponseEntity<?> createUser(
            @RequestParam
            @Email
            final String email,
            @RequestParam
            final String firstName,
            @RequestParam
            final String secondName
    ) {
        try {
            final UserId userId = userRepository.saveUser(new User(
                    null,
                    new UserEmail(email),
                    new UserName(firstName, secondName)
            ));
            return ResponseEntity.status(HttpStatus.CREATED).body(userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error processing user creation", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/user")
    public ResponseEntity<?> changeUser(
            @RequestParam
            final int userId,
            @RequestParam
            final String email,
            @RequestParam
            final String firstName,
            @RequestParam
            final String secondName
    ) {
        try {
            final UserId userIdObject = userRepository.saveUser(new User(
                    new UserId(userId),
                    new UserEmail(email),
                    new UserName(firstName, secondName)
            ));
            return ResponseEntity.ok(userIdObject);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error changing user", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(
            @RequestParam
            int userId
    ) {
        try {
            final User user = userRepository.findUserById(new UserId(userId));
            return ResponseEntity.ok().body(userToResponseEntity(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with mentioned id not found");
        } catch (Exception e) {
            log.error("Error fetching user", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        try {
            return ResponseEntity.ok(
                    userRepository
                            .getAllUsers()
                            .stream()
                            .map(this::userToResponseEntity)
                            .collect(Collectors.toList())
            );
        } catch (Exception e) {
            log.error("Error all fetching users", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(
            @RequestParam
            int userId
    ) {
        try {
            userRepository.deleteUser(new UserId(userId));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with mentioned id not found");
        } catch (Exception e) {
            log.error("Error deleting user", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private UserResponseEntity userToResponseEntity(User user) {
        return new UserResponseEntity(
                user.getUserId().getUserId(),
                user.getUserEmail().getEmail(),
                user.getUserName().getFirstName(),
                user.getUserName().getSecondName()
        );
    }
}
