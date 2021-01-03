package org.denysr.learning.office_booking.domain.booking;

import org.denysr.learning.office_booking.domain.user.UserId;
import org.denysr.learning.office_booking.domain.validation.IllegalValueException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @ParameterizedTest
    @MethodSource("provideInvalidBookingConstructorParams")
    void should_fail_for_invalid_constructor_params(
            BookingId bookingId, UserId userId, BookingDateRange bookingDateRange
    ) {
        assertThrows(IllegalValueException.class, () -> new Booking(bookingId, userId, bookingDateRange));
    }

    @Test
    void should_create_booking_without_id() {
        UserId userId = new UserId(7);
        BookingDateRange bookingDateRange = new BookingDateRange(
                LocalDate.of(2020, 5, 18),
                LocalDate.of(2020, 5, 28)
        );

        Booking booking = new Booking(null, userId, bookingDateRange);

        assertAll(
                () -> assertFalse(booking.hasId()),
                () -> assertNull(booking.getBookingId()),
                () -> assertEquals(userId, booking.getUserId()),
                () -> assertEquals(bookingDateRange, booking.getBookingDateRange())
        );
    }

    @Test
    void should_create_booking_with_id() {
        BookingId bookingId = new BookingId(9);
        UserId userId = new UserId(7);
        BookingDateRange bookingDateRange = new BookingDateRange(
                LocalDate.of(2018, 12, 18),
                LocalDate.of(2020, 3, 28)
        );

        Booking booking = new Booking(bookingId, userId, bookingDateRange);

        assertAll(
                () -> assertTrue(booking.hasId()),
                () -> assertEquals(bookingId, booking.getBookingId()),
                () -> assertEquals(userId, booking.getUserId()),
                () -> assertEquals(bookingDateRange, booking.getBookingDateRange())
        );
    }

    @Test
    void should_change_id_by_with_id() {
        BookingId bookingId = new BookingId(900);
        UserId userId = new UserId(78);
        BookingDateRange bookingDateRange = new BookingDateRange(
                LocalDate.of(2019, 12, 30),
                LocalDate.of(2020, 2, 2)
        );

        Booking booking = new Booking(bookingId, userId, bookingDateRange);

        assertAll(
                () -> assertTrue(booking.hasId()),
                () -> assertEquals(bookingId, booking.getBookingId()),
                () -> assertEquals(userId, booking.getUserId()),
                () -> assertEquals(bookingDateRange, booking.getBookingDateRange())
        );
    }

    private static Stream<Arguments> provideInvalidBookingConstructorParams() {
        BookingDateRange correctBookingDateRange = new BookingDateRange(
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2020, 2, 2)
        );
        return Stream.of(
                Arguments.of(null, null, null),
                Arguments.of(new BookingId(1), null, null),
                Arguments.of(new BookingId(2), new UserId(8), null),
                Arguments.of(new BookingId(3), null, correctBookingDateRange),
                Arguments.of(null, new UserId(3), null),
                Arguments.of(null, null, correctBookingDateRange)
        );
    }
}