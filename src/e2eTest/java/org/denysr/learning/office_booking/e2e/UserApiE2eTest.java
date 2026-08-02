package org.denysr.learning.office_booking.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * End-to-end coverage of the user path against the application running in a container.
 * <p>
 * Tests share one application instance and therefore one database, so every test registers its own
 * user under a unique email instead of relying on a known database state.
 */
@ExtendWith(ApplicationUnderTest.class)
class UserApiE2eTest {
    private static final AtomicInteger EMAIL_COUNTER = new AtomicInteger();

    private final UserApiClient api;

    UserApiE2eTest(UserApiClient api) {
        this.api = api;
    }

    @Test
    @DisplayName("A user can be registered, read back, changed and unregistered")
    void userLifecycle() {
        final String email = uniqueEmail();
        final ApiResponse created = api.createUser(new UserPayload(email, "John", "Doe"));
        assertThat(created.status()).as("create: %s", created).isEqualTo(201);

        final int userId = created.userId();
        assertThat(userId).isPositive();

        final ApiResponse fetched = api.getUser(userId);
        assertThat(fetched.status()).as("get: %s", fetched).isEqualTo(200);
        assertThat(fetched.json().path("userId").asInt()).isEqualTo(userId);
        assertThat(fetched.json().path("email").asText()).isEqualTo(email);
        assertThat(fetched.json().path("firstName").asText()).isEqualTo("John");
        assertThat(fetched.json().path("secondName").asText()).isEqualTo("Doe");

        final String changedEmail = uniqueEmail();
        final ApiResponse changed = api.changeUser(userId, new UserPayload(changedEmail, "Jane", "Roe"));
        assertThat(changed.status()).as("change: %s", changed).isEqualTo(200);
        assertThat(changed.userId()).isEqualTo(userId);

        final ApiResponse afterChange = api.getUser(userId);
        assertThat(afterChange.status()).isEqualTo(200);
        assertThat(afterChange.json().path("email").asText()).isEqualTo(changedEmail);
        assertThat(afterChange.json().path("firstName").asText()).isEqualTo("Jane");
        assertThat(afterChange.json().path("secondName").asText()).isEqualTo("Roe");

        final ApiResponse deleted = api.deleteUser(userId);
        assertThat(deleted.status()).as("delete: %s", deleted).isEqualTo(200);

        final ApiResponse remaining = api.getAllUsers();
        assertThat(findById(remaining.json(), userId)).as("user %d should be gone", userId).isNull();
    }

    @Test
    @DisplayName("A registered user shows up in the user list")
    void registeredUserIsListed() {
        final String email = uniqueEmail();
        final int userId = createUser(new UserPayload(email, "Lena", "Novak"));

        final ApiResponse users = api.getAllUsers();
        assertThat(users.status()).as("list: %s", users).isEqualTo(200);
        assertThat(users.json()).isNotEmpty();

        final JsonNode listed = findById(users.json(), userId);
        assertThat(listed).as("user %d in %s", userId, users.body()).isNotNull();
        assertThat(listed.path("email").asText()).isEqualTo(email);
        assertThat(listed.path("firstName").asText()).isEqualTo("Lena");
        assertThat(listed.path("secondName").asText()).isEqualTo("Novak");
    }

    @Test
    @DisplayName("An unregistered user disappears from the user list")
    void unregisteredUserIsNotListed() {
        final int userId = createUser(UserPayload.validUser(uniqueEmail()));

        assertThat(api.deleteUser(userId).status()).isEqualTo(200);

        final ApiResponse users = api.getAllUsers();
        assertThat(users.status()).isEqualTo(200);
        assertThat(findById(users.json(), userId)).as("user %d should be gone", userId).isNull();
    }

    @Test
    @DisplayName("Registration rejects a malformed email")
    void malformedEmailIsRejected() {
        final ApiResponse response = api.createUser(new UserPayload("not-an-email", "John", "Doe"));

        assertThat(response.status()).as("%s", response).isEqualTo(422);
        assertThat(response.error()).isEqualTo("Invalid email provided");
    }

    @Test
    @DisplayName("Registration rejects a name that is too short")
    void tooShortNameIsRejected() {
        final ApiResponse response = api.createUser(new UserPayload(uniqueEmail(), "J", "Doe"));

        assertThat(response.status()).as("%s", response).isEqualTo(422);
        assertThat(response.error()).isEqualTo("Name length should be between 2 and 70");
    }

    @Test
    @DisplayName("Registration rejects a name with non-latin characters")
    void nonLatinNameIsRejected() {
        final ApiResponse response = api.createUser(new UserPayload(uniqueEmail(), "John", "Доу"));

        assertThat(response.status()).as("%s", response).isEqualTo(422);
        assertThat(response.error()).isEqualTo("Name should contain only latin letters");
    }

    @Test
    @DisplayName("Registration rejects a blank name")
    void blankNameIsRejected() {
        final ApiResponse response = api.createUser(new UserPayload(uniqueEmail(), "", "Doe"));

        assertThat(response.status()).as("%s", response).isEqualTo(422);
        assertThat(response.error()).isEqualTo("User name should not be empty.");
    }

    @Test
    @DisplayName("Registration rejects an email shorter than the allowed minimum")
    void tooShortEmailIsRejected() {
        final ApiResponse response = api.createUser(new UserPayload("a@b.c", "John", "Doe"));

        assertThat(response.status()).as("%s", response).isEqualTo(422);
        assertThat(response.error()).isEqualTo("email Length should be between 6 and 100");
    }

    @Test
    @DisplayName("Changing a user rejects a malformed email")
    void changeWithMalformedEmailIsRejected() {
        final int userId = createUser(UserPayload.validUser(uniqueEmail()));

        final ApiResponse response = api.changeUser(userId, new UserPayload("still-not-an-email", "John", "Doe"));

        assertThat(response.status()).as("%s", response).isEqualTo(422);
        assertThat(response.error()).isEqualTo("Invalid email provided");
    }

    @Test
    @DisplayName("Reading a user with a non-positive id is rejected")
    void nonPositiveUserIdIsRejected() {
        final ApiResponse response = api.getUser(0);

        assertThat(response.status()).as("%s", response).isEqualTo(406);
        assertThat(response.error()).isEqualTo("User id should be above 0.");
    }

    @Test
    @DisplayName("Unregistering a user with a non-positive id is rejected")
    void deletingNonPositiveUserIdIsRejected() {
        final ApiResponse response = api.deleteUser(-1);

        assertThat(response.status()).as("%s", response).isEqualTo(406);
        assertThat(response.error()).isEqualTo("User id should be above 0.");
    }

    @Test
    @DisplayName("Unregistering an unknown user reports not found")
    void deletingUnknownUserReportsNotFound() {
        final ApiResponse response = api.deleteUser(Integer.MAX_VALUE);

        assertThat(response.status()).as("%s", response).isEqualTo(404);
    }

    @Test
    @DisplayName("Reading an unknown user reports not found")
    void readingUnknownUserReportsNotFound() {
        final ApiResponse response = api.getUser(Integer.MAX_VALUE);

        assertThat(response.status()).as("%s", response).isEqualTo(404);
    }

    @Test
    @DisplayName("Changing an unknown user reports not found")
    void changingUnknownUserReportsNotFound() {
        final ApiResponse response =
                api.changeUser(Integer.MAX_VALUE, UserPayload.validUser(uniqueEmail()));

        assertThat(response.status()).as("%s", response).isEqualTo(404);
    }

    @Test
    @DisplayName("Registering an email that is already taken is rejected")
    void duplicateEmailIsRejected() {
        final String email = uniqueEmail();
        createUser(new UserPayload(email, "First", "Owner"));

        final ApiResponse response = api.createUser(new UserPayload(email, "Second", "Owner"));

        assertThat(response.status()).as("%s", response).isBetween(400, 499);
    }

    private int createUser(UserPayload payload) {
        final ApiResponse created = api.createUser(payload);
        assertThat(created.status()).as("create: %s", created).isEqualTo(201);
        return created.userId();
    }

    private static JsonNode findById(JsonNode users, int userId) {
        for (JsonNode user : users) {
            if (user.path("userId").asInt() == userId) {
                return user;
            }
        }
        return null;
    }

    private static String uniqueEmail() {
        return "e2e.user." + EMAIL_COUNTER.incrementAndGet() + "@example.com";
    }
}
