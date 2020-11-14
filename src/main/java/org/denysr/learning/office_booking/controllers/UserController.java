package org.denysr.learning.office_booking.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.denysr.learning.office_booking.domain.user.*;
import org.denysr.learning.office_booking.domain.validation.EntityNotFoundException;
import org.denysr.learning.office_booking.domain.validation.IllegalValueException;
import org.denysr.learning.office_booking.infrastructure.rest.ErrorResponse;
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
final public class UserController {
    private final UserRepository userRepository;

    @Operation(summary = "Create user", description = "Creating a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserId.class))),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid parameter(s) supplied",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Unknown server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/user", consumes = {"application/json"})
    public ResponseEntity<?> createUser(
            @Parameter(description = "Email of the user", required = true, example = "john.doe@example.com")
            @Email
            final String email,
            @Parameter(description = "User's first name", required = true, example = "John")
            final String firstName,
            @Parameter(description = "User's surname", required = true, example = "Doe")
            final String secondName
    ) {
        try {
            final UserId userId = userRepository.saveUser(new User(
                    null,
                    new UserEmail(email),
                    new UserName(firstName, secondName)
            ));
            return ResponseEntity.status(HttpStatus.CREATED).body(userId);
        } catch (IllegalValueException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error processing user creation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(""));
        }
    }

    @Operation(summary = "Change user", description = "Changing existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully changed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserId.class))),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid parameter(s) supplied",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "User with mentioned id not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unknown server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/user", consumes = {"application/json"})
    public ResponseEntity<?> changeUser(
            @Parameter(description = "User id", required = true, example = "3")
            final int userId,
            @Parameter(description = "Email of the user", required = true, example = "john.doe@example.com")
            @Email
            final String email,
            @Parameter(description = "User's first name", required = true, example = "John")
            final String firstName,
            @Parameter(description = "User's surname", required = true, example = "Doe")
            final String secondName
    ) {
        try {
            final UserId userIdObject = userRepository.saveUser(new User(
                    new UserId(userId),
                    new UserEmail(email),
                    new UserName(firstName, secondName)
            ));
            return ResponseEntity.ok(userIdObject);
        } catch (IllegalValueException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ErrorResponse(e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error changing user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(""));
        }
    }

    @Operation(summary = "Get user", description = "Get user by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found and returned", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseEntity.class)
            )),
            @ApiResponse(responseCode = "404", description = "User with mentioned id not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "406", description = "Invalid parameter supplied",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unknown server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/user")
    public ResponseEntity<?> getUser(
            @RequestParam
            int userId
    ) {
        try {
            final User user = userRepository.findUserById(new UserId(userId));
            return ResponseEntity.ok().body(userToResponseEntity(user));
        } catch (IllegalValueException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorResponse(e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(""));
        }
    }

    @Operation(summary = "Get all users", description = "Get all users, existing in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseEntity[].class)
            )),
            @ApiResponse(responseCode = "500", description = "Unknown server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
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
            log.error("Error fetching all users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(""));
        }
    }

    @Operation(summary = "Delete user", description = "Delete user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "User with mentioned id not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "406", description = "Invalid parameter supplied",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unknown server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping(value = "/user", consumes = {"application/json"})
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "User id", required = true, example = "3")
            int userId
    ) {
        try {
            userRepository.deleteUser(new UserId(userId));
            return ResponseEntity.ok().build();
        } catch (IllegalValueException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorResponse(e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(""));
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
