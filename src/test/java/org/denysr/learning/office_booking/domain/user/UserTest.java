package org.denysr.learning.office_booking.domain.user;

import org.denysr.learning.office_booking.domain.validation.IllegalValueException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @ParameterizedTest
    @MethodSource("provideInvalidUserConstructorParams")
    void should_fail_for_invalid_constructor_params(
            UserId userId, UserEmail userEmail, UserName userName
    ) {
        assertThrows(IllegalValueException.class, () -> new User(userId, userEmail, userName));
    }

    @Test
    void should_create_user_without_id() {
        UserEmail userEmail = new UserEmail("ww@ww.ua");
        UserName userName = new UserName("tre", "qwe");

        User user = new User(null, userEmail, userName);

        assertAll(
                () -> assertFalse(user.hasId()),
                () -> assertNull(user.getUserId()),
                () -> assertEquals(userEmail, user.getUserEmail()),
                () -> assertEquals(userName, user.getUserName())
        );
    }

    @Test
    void should_create_user_with_id() {
        UserId userId = new UserId(4);
        UserEmail userEmail = new UserEmail("real@gmx.de");
        UserName userName = new UserName("Fritz", "Kafka");

        User user = new User(userId, userEmail, userName);

        assertAll(
                () -> assertTrue(user.hasId()),
                () -> assertEquals(userId, user.getUserId()),
                () -> assertEquals(userEmail, user.getUserEmail()),
                () -> assertEquals(userName, user.getUserName())
        );
    }

    @Test
    void should_change_id_by_with_id() {
        UserId userId = new UserId(4);
        UserEmail userEmail = new UserEmail("smthng@gmx.br");
        UserName userName = new UserName("Ivan", "Pupkin");

        User user = new User(null, userEmail, userName).withUserId(userId);

        assertAll(
                () -> assertTrue(user.hasId()),
                () -> assertEquals(userId, user.getUserId()),
                () -> assertEquals(userEmail, user.getUserEmail()),
                () -> assertEquals(userName, user.getUserName())
        );
    }

    private static Stream<Arguments> provideInvalidUserConstructorParams() {
        return Stream.of(
                Arguments.of(null, null, null),
                Arguments.of(new UserId(4), null, null),
                Arguments.of(new UserId(4), new UserEmail("smith@email.com"), null),
                Arguments.of(new UserId(4), null, new UserName("John", "Doe")),
                Arguments.of(null, new UserEmail("smith@email.com"), null),
                Arguments.of(null, null, new UserName("John", "Doe"))
        );
    }
}