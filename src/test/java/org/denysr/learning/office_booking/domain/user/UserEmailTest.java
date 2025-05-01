package org.denysr.learning.office_booking.domain.user;

import org.denysr.learning.office_booking.domain.validation.IllegalValueException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserEmailTest {
    @ParameterizedTest
    @MethodSource("provideCorrectEmail")
    void should_create_email_for_correct_param(String emailParam, String expectedEmail) {
        UserEmail userEmail = new UserEmail(emailParam);
        assertEquals(expectedEmail, userEmail.email());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidEmail")
    void should_throw_exception_for_incorrect_param(String email) {
        assertThrows(IllegalValueException.class, () -> new UserEmail(email));
    }

    private static Stream<Arguments> provideCorrectEmail() {
        return Stream.of(
                Arguments.of("pupkin@email.ua", "pupkin@email.ua"),
                Arguments.of("   pupkin@email.com", "pupkin@email.com"),
                Arguments.of("pupkin@email.de ", "pupkin@email.de"),
                Arguments.of("    pupkin@email.ua   ", "pupkin@email.ua"),
                Arguments.of("angela.merkel@email.de ", "angela.merkel@email.de")
        );
    }

    private static Stream<Arguments> provideInvalidEmail() {
        return Stream.of(
                null,
                Arguments.of(""),
                Arguments.of("simple string"),
                Arguments.of("some.email@domain.po")
        );
    }
}