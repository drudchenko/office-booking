package org.denysr.learning.office_booking.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.denysr.learning.office_booking.domain.user.*;
import org.denysr.learning.office_booking.domain.validation.EntityNotFoundException;
import org.denysr.learning.office_booking.infrastructure.rest.UserRestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class UserControllerIT {

    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private UserRepository userRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void whenCreateUser_givenCorrectParams_shouldReturnUserId() throws Exception {
        String email = "correct@email.com";
        String firstName = "John";
        String secondName = "Doe";

        User expectedUser = new User(null, new UserEmail(email), new UserName(firstName, secondName));
        int expectedUserId = 5;

        when(userRepository.saveUser(expectedUser)).thenReturn(new UserId(expectedUserId));

        UserRestDto userRequest = new UserRestDto(email, firstName, secondName);

        mvc.perform(post("/users/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", is(expectedUserId)));
    }

    @Test
    void whenCreateUser_givenIncorrectParams_shouldThrowUnprocessableEntityException() throws Exception {
        String email = "correct@email.c";
        String firstName = "John";
        String secondName = "Doe";

        UserRestDto userRequest = new UserRestDto(email, firstName, secondName);

        mvc.perform(post("/users/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error", is("Invalid email provided")));
    }

    @Test
    void whenCreateUser_givenUnexpectedError_shouldNotReturnMessage() throws Exception {
        String email = "correct@email.com";
        String firstName = "John";
        String secondName = "Doe";
        User expectedUser = new User(null, new UserEmail(email), new UserName(firstName, secondName));

        when(userRepository.saveUser(expectedUser)).thenThrow(new RuntimeException("Very strange error!"));

        UserRestDto userRequest = new UserRestDto(email, firstName, secondName);

        mvc.perform(post("/users/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("")));
    }

    @Test
    void whenChangeUser_givenCorrectParams_shouldReturnUserId() throws Exception {
        int userId = 10;
        String email = "correct@email.com";
        String firstName = "John";
        String secondName = "Doe";

        User expectedUser = new User(new UserId(userId), new UserEmail(email), new UserName(firstName, secondName));
        when(userRepository.saveUser(expectedUser)).thenReturn(new UserId(userId));

        UserRestDto userRequest = new UserRestDto(email, firstName, secondName);

        mvc.perform(put("/users/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(userId)));
    }

    @Test
    void whenChangeUser_givenIncorrectParams_shouldThrowUnprocessableEntityException() throws Exception {
        int userId = 17;
        String email = "correct@email.com";
        String firstName = "J";
        String secondName = "Doe";

        UserRestDto userRequest = new UserRestDto(email, firstName, secondName);

        mvc.perform(put("/users/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error", is("Name length should be between 2 and 70")));
    }

    @Test
    void whenChangeUser_givenNonExistentUserId_shouldThrowNotFoundException() throws Exception {
        int userId = 10;
        String email = "correct@email.com";
        String firstName = "Johny";
        String secondName = "Doe";
        String errorMessage = "test message";

        User expectedUser = new User(new UserId(userId), new UserEmail(email), new UserName(firstName, secondName));
        when(userRepository.saveUser(expectedUser)).thenThrow(new EntityNotFoundException(errorMessage));

        UserRestDto userRequest = new UserRestDto(email, firstName, secondName);

        mvc.perform(put("/users/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(errorMessage)));
    }

    @Test
    void whenChangeUser_givenUnexpectedError_shouldNotReturnMessage() throws Exception {
        int userId = 10;
        String email = "correct@email.com";
        String firstName = "Johny";
        String secondName = "Doe";

        User expectedUser = new User(new UserId(userId), new UserEmail(email), new UserName(firstName, secondName));
        when(userRepository.saveUser(expectedUser)).thenThrow(new RuntimeException("test message"));

        UserRestDto userRequest = new UserRestDto(email, firstName, secondName);

        mvc.perform(put("/users/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("")));
    }

    @Test
    void whenGetUser_givenCorrectParams_shouldReturnUser() throws Exception {
        int userId = 15;
        String email = "correct@email.com";
        String firstName = "John";
        String secondName = "Doe";

        User expectedUser = new User(new UserId(userId), new UserEmail(email), new UserName(firstName, secondName));

        when(userRepository.findUserById(new UserId(userId))).thenReturn(expectedUser);

        mvc.perform(get("/users/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(userId)))
                .andExpect(jsonPath("$.email", is(email)))
                .andExpect(jsonPath("$.firstName", is(firstName)))
                .andExpect(jsonPath("$.secondName", is(secondName)));
    }

    @Test
    void whenGetUser_givenNonExistentId_shouldReturnNotFoundCode() throws Exception {
        int userId = 15;
        String errorMessage = "No user found";

        when(userRepository.findUserById(new UserId(userId))).thenThrow(new EntityNotFoundException(errorMessage));

        mvc.perform(get("/users/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(errorMessage)));
    }

    @Test
    void whenGetUser_givenWrongId_shouldReturnNotAcceptableCode() throws Exception {
        int userId = -1;

        mvc.perform(get("/users/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.error", is("User id should be above 0.")));
    }

    @Test
    void whenGetUser_givenUnexpectedError_shouldNotReturnMessage() throws Exception {
        int userId = 15;

        when(userRepository.findUserById(new UserId(userId))).thenThrow(new RuntimeException("dummy err msg"));

        mvc.perform(get("/users/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("")));
    }

    @Test
    void whenGetUsers_shouldReturnUserList() throws Exception {
        int userId1 = 15;
        String email1 = "correct@email.com";
        String firstName1 = "John";
        String secondName1 = "Doe";
        User user1 = new User(new UserId(userId1), new UserEmail(email1), new UserName(firstName1, secondName1));

        int userId2 = 15;
        String email2 = "correct@email.com";
        String firstName2 = "John";
        String secondName2 = "Doe";
        User user2 = new User(new UserId(userId2), new UserEmail(email2), new UserName(firstName2, secondName2));

        when(userRepository.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        mvc.perform(get("/users/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userId", is(userId1)))
                .andExpect(jsonPath("$[0].email", is(email1)))
                .andExpect(jsonPath("$[0].firstName", is(firstName1)))
                .andExpect(jsonPath("$[0].secondName", is(secondName1)))
                .andExpect(jsonPath("$[1].userId", is(userId2)))
                .andExpect(jsonPath("$[1].email", is(email2)))
                .andExpect(jsonPath("$[1].firstName", is(firstName2)))
                .andExpect(jsonPath("$[1].secondName", is(secondName2)));

    }

    @Test
    void whenGetUsers_givenUnexpectedError_shouldNotReturnMessage() throws Exception {
        when(userRepository.getAllUsers()).thenThrow(new RuntimeException("yet another error"));

        mvc.perform(get("/users/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("")));
    }

    @Test
    void whenDeleteUser_givenCorrectParam_shouldReturnSuccessStatus() throws Exception {
        int userId = 10;

        mvc.perform(delete("/users/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDeleteUser_givenIncorrectParam_shouldReturnNotAcceptableStatus() throws Exception {
        int userId = 0;

        mvc.perform(delete("/users/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void whenDeleteUser_givenNonExistentUserId_shouldReturnNotAcceptableStatus() throws Exception {
        int userId = 6;
        String errorMessage = "entity not found";

        doThrow(new EntityNotFoundException(errorMessage)).when(userRepository).deleteUser(new UserId(userId));

        mvc.perform(delete("/users/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(errorMessage)));
    }

    @Test
    void whenDeleteUser_givenUnexpectedError_shouldNotReturnMessage() throws Exception {
        int userId = 6;

        doThrow(new RuntimeException("Spooky error")).when(userRepository).deleteUser(new UserId(userId));

        mvc.perform(delete("/users/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("")));
    }
}