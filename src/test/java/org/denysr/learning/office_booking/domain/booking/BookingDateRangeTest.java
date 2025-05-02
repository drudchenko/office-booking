package org.denysr.learning.office_booking.domain.booking;

import org.denysr.learning.office_booking.domain.date.DateRange;
import org.denysr.learning.office_booking.domain.validation.IllegalValueException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingDateRangeTest {

    @ParameterizedTest
    @MethodSource("provideCorrectBookingDateRanges")
    void should_create_daterange_for_correct_dates(LocalDate startDate, LocalDate endDate) {
        BookingDateRange bookingDateRange = new BookingDateRange(startDate, endDate);
        assertAll(
                () -> assertEquals(startDate, bookingDateRange.getStartDate()),
                () -> assertEquals(endDate, bookingDateRange.getEndDate())
        );
    }

    @ParameterizedTest
    @MethodSource("provideIllegalBookingDateRanges")
    void should_throw_exceptions_for_incorrect_dates(LocalDate startDate, LocalDate endDate) {
        assertThrows(IllegalValueException.class, () -> new BookingDateRange(startDate, endDate));
    }

    @ParameterizedTest
    @MethodSource("provideIntersectingRanges")
    void should_detect_intersected_ranges(LocalDate startDate1, LocalDate endDate1, LocalDate startDate2, LocalDate endDate2) {
        BookingDateRange range1 = new BookingDateRange(startDate1, endDate1);
        BookingDateRange range2 = new BookingDateRange(startDate2, endDate2);
        assertAll(
                () -> assertTrue(range1.intersectsWith(range2.dateRange())),
                () -> assertTrue(range2.intersectsWith(range1.dateRange()))
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonIntersectingRanges")
    void should_detect_non_intersected_ranges(LocalDate startDate1, LocalDate endDate1, LocalDate startDate2, LocalDate endDate2) {
        BookingDateRange range1 = new BookingDateRange(startDate1, endDate1);
        BookingDateRange range2 = new BookingDateRange(startDate2, endDate2);
        assertAll(
                () -> assertFalse(range1.intersectsWith(range2.dateRange())),
                () -> assertFalse(range2.intersectsWith(range1.dateRange()))
        );
    }

    @ParameterizedTest
    @MethodSource("provideCorrectRangeWeekData")
    void should_provide_correct_range_for_week(
            LocalDate startDateParam,
            LocalDate endDateParam,
            BusinessWeek businessWeek,
            LocalDate expectedRangeStart,
            LocalDate expectedRangeEnd
    ) {
        BookingDateRange bookingDateRange = new BookingDateRange(startDateParam, endDateParam);
        DateRange rangeWeek = bookingDateRange.getRangeForWeek(businessWeek);
        assertAll(
                () -> assertEquals(expectedRangeStart, rangeWeek.startDate()),
                () -> assertEquals(expectedRangeEnd, rangeWeek.endDate())
        );
    }

    @Test
    void should_fail_detect_week_range_for_wrong_week() {
        BookingDateRange bookingDateRange = new BookingDateRange(
                LocalDate.of(2020, 10, 10),
                LocalDate.of(2020, 10, 15)
        );
        BusinessWeek businessWeek = new BusinessWeek(LocalDate.of(2020, 10, 1));
        assertThrows(IllegalValueException.class, () -> bookingDateRange.getRangeForWeek(businessWeek));
    }

    private static Stream<Arguments> provideCorrectBookingDateRanges() {
        return Stream.of(
                Arguments.of(LocalDate.of(2020, 2, 3), LocalDate.of(2020, 2, 3)),
                Arguments.of(LocalDate.of(2019, 10, 15), LocalDate.of(2020, 10, 17)),
                Arguments.of(LocalDate.of(1900, 1, 1), LocalDate.of(2200, 1, 1))
        );
    }

    private static Stream<Arguments> provideIllegalBookingDateRanges() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(LocalDate.of(2010, 12, 11), null),
                Arguments.of(null, LocalDate.of(2015, 8, 12)),
                Arguments.of(LocalDate.of(2019, 9, 9), LocalDate.of(2019, 9, 8)),
                // No business days in range
                Arguments.of(LocalDate.of(2020, 2, 1), LocalDate.of(2020, 2, 1)),
                Arguments.of(LocalDate.of(2200, 1, 1), LocalDate.of(1900, 1, 1))
        );
    }

    private static Stream<Arguments> provideIntersectingRanges() {
        return Stream.of(
                Arguments.of(
                        LocalDate.of(2020, 10, 26), LocalDate.of(2020, 10, 26),
                        LocalDate.of(2020, 10, 26), LocalDate.of(2020, 10, 26)
                ),
                Arguments.of(
                        LocalDate.of(2019, 1, 21), LocalDate.of(2019, 1, 28),
                        LocalDate.of(2019, 1, 28), LocalDate.of(2019, 2, 25)
                ),
                Arguments.of(
                        LocalDate.of(1990, 1, 21), LocalDate.of(1990, 1, 28),
                        LocalDate.of(1990, 1, 23), LocalDate.of(1990, 2, 25)
                ),
                Arguments.of(
                        LocalDate.of(2000, 1, 21), LocalDate.of(2000, 10, 28),
                        LocalDate.of(2000, 5, 28), LocalDate.of(2000, 6, 25)
                )
        );
    }

    private static Stream<Arguments> provideNonIntersectingRanges() {
        return Stream.of(
                Arguments.of(
                        LocalDate.of(2020, 10, 26), LocalDate.of(2020, 10, 26),
                        LocalDate.of(2020, 10, 27), LocalDate.of(2020, 10, 27)
                ),
                Arguments.of(
                        LocalDate.of(2020, 10, 21), LocalDate.of(2020, 10, 25),
                        LocalDate.of(2020, 10, 26), LocalDate.of(2020, 10, 30)
                ),
                Arguments.of(
                        LocalDate.of(2000, 10, 21), LocalDate.of(2000, 10, 25),
                        LocalDate.of(2020, 10, 26), LocalDate.of(2020, 10, 30)
                )
        );
    }

    private static Stream<Arguments> provideCorrectRangeWeekData() {
        return Stream.of(
                Arguments.of(
                        LocalDate.of(2020, 10, 21), LocalDate.of(2020, 11, 13),
                        new BusinessWeek(LocalDate.of(2020, 10, 27)),
                        LocalDate.of(2020, 10, 26), LocalDate.of(2020, 10, 30)
                ),
                Arguments.of(
                        LocalDate.of(2020, 10, 28), LocalDate.of(2020, 11, 30),
                        new BusinessWeek(LocalDate.of(2020, 10, 27)),
                        LocalDate.of(2020, 10, 28), LocalDate.of(2020, 10, 30)
                ),
                Arguments.of(
                        LocalDate.of(2020, 10, 26), LocalDate.of(2020, 10, 26),
                        new BusinessWeek(LocalDate.of(2020, 10, 27)),
                        LocalDate.of(2020, 10, 26), LocalDate.of(2020, 10, 26)
                )
        );
    }
}