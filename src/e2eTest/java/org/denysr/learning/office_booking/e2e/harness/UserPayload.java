package org.denysr.learning.office_booking.e2e.harness;

/** Request body of the user endpoints, declared independently of the application classes. */
public record UserPayload(String email, String firstName, String secondName) {

    public static UserPayload validUser(String email) {
        return new UserPayload(email, "John", "Doe");
    }
}
