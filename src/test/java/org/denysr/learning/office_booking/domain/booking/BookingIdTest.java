package org.denysr.learning.office_booking.domain.booking;

import org.denysr.learning.office_booking.domain.validation.IllegalValueException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookingIdTest {
    @ParameterizedTest
    @MethodSource("provideCorrectIds")
    void booking_id_should_be_created_with_proper_values(int value) {
        final BookingId bookingId = new BookingId(value);
        assertEquals(value, bookingId.getBookingId());
    }

    @ParameterizedTest
    @MethodSource("provideIncorrectIds")
    void booking_id_should_fail_validation_with_invalid_values(int value) {
        assertThrows(IllegalValueException.class, () -> new BookingId(value));
    }

    private static Stream<Arguments> provideCorrectIds() {
        return Stream.of(
                Arguments.of(1),
                Arguments.of(5),
                Arguments.of(1009),
                Arguments.of(90000034),
                Arguments.of(Integer.MAX_VALUE)
        );
    }

    private static Stream<Arguments> provideIncorrectIds() {
        return Stream.of(
                Arguments.of(0),
                Arguments.of(-5),
                Arguments.of(Integer.MAX_VALUE * 2),
                Arguments.of(-90000034),
                Arguments.of(Integer.MIN_VALUE)
        );
    }
}