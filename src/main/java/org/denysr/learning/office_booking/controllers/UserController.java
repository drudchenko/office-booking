package org.denysr.learning.office_booking.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.denysr.learning.office_booking.domain.user.User;
import org.denysr.learning.office_booking.domain.user.User.UserBuilder;
import org.denysr.learning.office_booking.domain.user.UserId;
import org.denysr.learning.office_booking.domain.user.UserRepository;
import org.denysr.learning.office_booking.domain.validation.EntityNotFoundException;
import org.denysr.learning.office_booking.domain.validation.IllegalValueException;
import org.denysr.learning.office_booking.infrastructure.rest.ErrorResponse;
import org.denysr.learning.office_booking.infrastructure.rest.UserResponseEntity;
import org.denysr.learning.office_booking.infrastructure.rest.UserRestDto;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Log4j2
final public class UserController {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Operation(summary = "Create user", description = "Creating a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully created",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserId.class))),
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
            @RequestBody
            UserRestDto userDto
    ) {
        try {
            final UserBuilder userBuilder = modelMapper.map(userDto, UserBuilder.class);
            final UserId userId = userRepository.saveUser(userBuilder.build());
            return ResponseEntity.status(HttpStatus.CREATED).body(userId);
        } catch (MappingException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ErrorResponse(e.getCause().getMessage()));
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
    @PutMapping(value = "/user/{userId}", consumes = {"application/json"})
    public ResponseEntity<?> changeUser(
            @PathVariable("userId")
            @Parameter(description = "Id of the existing user, which is being changed", required = true, example = "4")
            final int userId,
            @RequestBody
            UserRestDto userDto
    ) {
        try {
            final UserBuilder userBuilder = modelMapper.map(userDto, UserBuilder.class)
                    .withUserId(new UserId(userId));
            final UserId userIdObject = userRepository.saveUser(userBuilder.build());
            return ResponseEntity.ok(userIdObject);
        } catch (MappingException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ErrorResponse(e.getCause().getMessage()));
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
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUser(
            @PathVariable("userId")
            @Parameter(description = "Id of the user to retrieve", required = true, example = "5")
            int userId
    ) {
        try {
            final User user = userRepository.findUserById(new UserId(userId));
            return ResponseEntity.ok().body(modelMapper.map(user, UserResponseEntity.class));
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
                    array = @ArraySchema(schema = @Schema(implementation = UserResponseEntity.class))
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
                            .map(user -> modelMapper.map(user, UserResponseEntity.class))
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
    @DeleteMapping(value = "/user/{userId}")
    public ResponseEntity<?> deleteUser(
            @PathVariable("userId")
            @Parameter(description = "Id of the existing user, which should be deleted", required = true, example = "4")
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
}
