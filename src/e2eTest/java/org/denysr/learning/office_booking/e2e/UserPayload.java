package org.denysr.learning.office_booking.e2e;

/** Request body of the user endpoints, declared independently of the application classes. */
record UserPayload(String email, String firstName, String secondName) {

    static UserPayload validUser(String email) {
        return new UserPayload(email, "John", "Doe");
    }
}
