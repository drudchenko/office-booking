package org.denysr.learning.office_booking.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class UserSanityCheckE2ETest {

    private static final int APP_PORT = 8080;
    private static final String APP_SERVICE_NAME = "app";

    @Container
    private static final ComposeContainer compose = new ComposeContainer(new File("docker-compose.yml"))
            .withExposedService(APP_SERVICE_NAME, APP_PORT);

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    void testUserCreationAndRetrieval() {
        String appHost = compose.getServiceHost(APP_SERVICE_NAME, APP_PORT);
        Integer appPort = compose.getServicePort(APP_SERVICE_NAME, APP_PORT);
        String baseUrl = String.format("http://%s:%d", appHost, appPort);

        // Create user
        String createUserUrl = baseUrl + "/users/user";
        Map<String, String> createUserRequest = Map.of(
                "firstName", "John",
                "secondName", "Doe",
                "email", "john.doe@example.com"
        );
        Map<String, Number> createUserResponse = restTemplate.postForObject(createUserUrl, createUserRequest, Map.class);
        Number userId = createUserResponse.get("userId");

        // Retrieve user
        String getUserUrl = baseUrl + "/users/user/" + userId;
        Map<String, Object> getUserResponse = restTemplate.getForObject(getUserUrl, Map.class);

        // Assertions
        assertEquals(userId, getUserResponse.get("userId"));
        assertEquals("John", getUserResponse.get("firstName"));
        assertEquals("Doe", getUserResponse.get("secondName"));
        assertEquals("john.doe@example.com", getUserResponse.get("email"));
    }
}
