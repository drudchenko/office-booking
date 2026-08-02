package org.denysr.learning.office_booking.e2e;

/** Client for the {@code /users} API. */
final class UserApiClient {
    private final HttpCalls http;

    UserApiClient(String baseUrl) {
        this.http = new HttpCalls(baseUrl);
    }

    ApiResponse createUser(UserPayload user) {
        return http.send(http.request("/users/user").POST(HttpCalls.jsonBody(user)));
    }

    ApiResponse changeUser(int userId, UserPayload user) {
        return http.send(http.request("/users/user/" + userId).PUT(HttpCalls.jsonBody(user)));
    }

    ApiResponse getUser(int userId) {
        return http.send(http.request("/users/user/" + userId).GET());
    }

    ApiResponse getAllUsers() {
        return http.send(http.request("/users/users").GET());
    }

    ApiResponse deleteUser(int userId) {
        return http.send(http.request("/users/user/" + userId).DELETE());
    }
}
