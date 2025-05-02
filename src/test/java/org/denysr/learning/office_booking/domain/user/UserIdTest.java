package org.denysr.learning.office_booking.domain.user;

import org.denysr.learning.office_booking.domain.booking.BookingId;
import org.denysr.learning.office_booking.domain.validation.IllegalValueException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserIdTest {

    @ParameterizedTest
    @MethodSource("provideCorrectIds")
    void user_id_should_be_created_with_proper_values(int value) {
        final BookingId bookingId = new BookingId(value);
        assertEquals(value, bookingId.bookingId());
    }

    @ParameterizedTest
    @MethodSource("provideIncorrectIds")
    void user_id_should_fail_validation_with_invalid_values(int value) {
        assertThrows(IllegalValueException.class, () -> new BookingId(value));
    }

    private static Stream<Arguments> provideCorrectIds() {
        return Stream.of(
                Arguments.of(1),
                Arguments.of(65),
                Arguments.of(9008),
                Arguments.of(90004096),
                Arguments.of(Integer.MAX_VALUE)
        );
    }

    private static Stream<Arguments> provideIncorrectIds() {
        return Stream.of(
                Arguments.of(0),
                Arguments.of(-2),
                Arguments.of(Integer.MAX_VALUE * 2),
                Arguments.of(-90065012),
                Arguments.of(Integer.MIN_VALUE)
        );
    }
}