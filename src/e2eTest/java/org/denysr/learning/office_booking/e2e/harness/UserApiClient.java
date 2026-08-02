package org.denysr.learning.office_booking.e2e.harness;

/** Client for the {@code /users} API. */
public final class UserApiClient implements ApiClient {
    private final HttpCalls http;

    public UserApiClient(String baseUrl) {
        this.http = new HttpCalls(baseUrl);
    }

    public ApiResponse createUser(UserPayload user) {
        return http.send(http.request("/users/user").POST(HttpCalls.jsonBody(user)));
    }

    public ApiResponse changeUser(int userId, UserPayload user) {
        return http.send(http.request("/users/user/" + userId).PUT(HttpCalls.jsonBody(user)));
    }

    public ApiResponse getUser(int userId) {
        return http.send(http.request("/users/user/" + userId).GET());
    }

    public ApiResponse getAllUsers() {
        return http.send(http.request("/users/users").GET());
    }

    public ApiResponse deleteUser(int userId) {
        return http.send(http.request("/users/user/" + userId).DELETE());
    }
}
