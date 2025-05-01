package org.denysr.learning.office_booking.domain.user;

import org.denysr.learning.office_booking.domain.validation.IllegalValueException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserNameTest {

    @ParameterizedTest
    @MethodSource("provideCorrectUserNames")
    void should_create_username_for_correct_params(
            String firstNameParam,
            String secondNameParam,
            String expectedFirstName,
            String expectedSecondName,
            String expectedFullName
    ) {
        UserName userName = new UserName(firstNameParam, secondNameParam);
        assertAll(
                () -> assertEquals(expectedFirstName, userName.firstName()),
                () -> assertEquals(expectedSecondName, userName.secondName()),
                () -> assertEquals(expectedFullName, userName.getFullName())
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUserNames")
    void should_throw_exception_for_incorrect_params(String firstName, String secondName) {
        assertThrows(IllegalValueException.class, () -> new UserName(firstName, secondName));
    }

    private static Stream<Arguments> provideCorrectUserNames() {
        String maxLengthName = "Johnatanis Johnatanis Johnatanis Johnatanis Johnatanis Johnatanis John";
        return Stream.of(
                Arguments.of("John", "Smith", "John", "Smith", "John Smith"),
                Arguments.of("   Johnatan    ", "   Smith Wandau   ", "Johnatan", "Smith Wandau", "Johnatan Smith Wandau"),
                Arguments.of("to", "tw", "to", "tw", "to tw"),
                Arguments.of("Sally", maxLengthName, "Sally", maxLengthName, "Sally " + maxLengthName),
                Arguments.of(maxLengthName, "Obama", maxLengthName, "Obama", maxLengthName + " Obama")
        );
    }

    private static Stream<Arguments> provideInvalidUserNames() {
        final String veryLongName = "Johny John Johny John Johny John Johny John Johny John Johny John Johny John " +
                "Johny John Johny John Johny John Johny John Johny John Johny John Johny John Johny John Johny John ";
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, "Smith"),
                Arguments.of("John", null),
                Arguments.of("John::", "Correctname"),
                Arguments.of("", "Correctname"),
                Arguments.of("Василий", "Пупкин"),
                Arguments.of("Correctname", "r"),
                Arguments.of("Correctname", "rrtesdfxr>"),
                Arguments.of("Correctname", veryLongName),
                Arguments.of(veryLongName, "Correctname"),
                Arguments.of(generateExtraLargeString(), "Biden"),
                Arguments.of("Frank", generateExtraLargeString())
        );
    }

    private static String generateExtraLargeString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (long i = 0; i < 10_000; i++) {
            stringBuilder.append("some really correct string is here");
        }
        return stringBuilder.toString();
    }
}