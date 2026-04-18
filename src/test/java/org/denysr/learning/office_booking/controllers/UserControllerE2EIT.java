package org.denysr.learning.office_booking.controllers;

import org.denysr.learning.office_booking.domain.user.UserId;
import org.denysr.learning.office_booking.infrastructure.rest.UserResponseEntity;
import org.denysr.learning.office_booking.infrastructure.rest.UserRestDto;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerE2EIT {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("office_booking_test")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static Integer firstUserId;
    private static Integer secondUserId;
    private static Integer thirdUserId;

    @Test
    @Order(1)
    void shouldCreateUsers() throws Exception {
        UserRestDto user1 = new UserRestDto("user1@example.com", "First", "User");
        MvcResult result1 = mockMvc.perform(post("/users/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isCreated())
                .andReturn();
        UserId u1 = objectMapper.readValue(result1.getResponse().getContentAsString(), UserId.class);
        firstUserId = u1.userId();

        UserRestDto user2 = new UserRestDto("user2@example.com", "Second", "User");
        MvcResult result2 = mockMvc.perform(post("/users/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isCreated())
                .andReturn();
        UserId u2 = objectMapper.readValue(result2.getResponse().getContentAsString(), UserId.class);
        secondUserId = u2.userId();

        UserRestDto user3 = new UserRestDto("user3@example.com", "Third", "User");
        MvcResult result3 = mockMvc.perform(post("/users/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user3)))
                .andExpect(status().isCreated())
                .andReturn();
        UserId u3 = objectMapper.readValue(result3.getResponse().getContentAsString(), UserId.class);
        thirdUserId = u3.userId();
    }

    @Test
    @Order(2)
    void shouldGetUsers() throws Exception {
        mockMvc.perform(get("/users/user/" + firstUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user1@example.com"));

        mockMvc.perform(get("/users/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @Order(3)
    void shouldChangeUser() throws Exception {
        UserRestDto changedUser = new UserRestDto("changed@example.com", "Changed", "User");
        
        mockMvc.perform(put("/users/user/" + secondUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changedUser)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/user/" + secondUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("changed@example.com"));
    }

    @Test
    @Order(4)
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/user/" + thirdUserId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/user/" + thirdUserId))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/users/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
