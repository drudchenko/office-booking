package org.denysr.learning.office_booking.domain.date;

import org.denysr.learning.office_booking.domain.validation.IllegalValueException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DateRangeTest {

    @ParameterizedTest
    @MethodSource("provideCorrectDateRanges")
    void should_create_daterange_for_correct_dates(LocalDate startDate, LocalDate endDate) {
        DateRange dateRange = new DateRange(startDate, endDate);
        assertAll(
                () -> assertEquals(startDate, dateRange.getStartDate()),
                () -> assertEquals(endDate, dateRange.getEndDate())
        );
    }

    @ParameterizedTest
    @MethodSource("provideIllegalDateRanges")
    void should_throw_exceptions_for_incorrect_dates(LocalDate startDate, LocalDate endDate) {
        assertThrows(IllegalValueException.class, () -> new DateRange(startDate, endDate));
    }

    @ParameterizedTest
    @MethodSource("provideRangeWithBusinessDays")
    void should_detect_business_days_in_range(LocalDate startDate, LocalDate endDate) {
        DateRange dateRange = new DateRange(startDate, endDate);
        assertTrue(dateRange.hasBusinessDayInRange());
    }

    @ParameterizedTest
    @MethodSource("provideRangeWithoutBusinessDays")
    void should_detect_business_days_absence_in_range(LocalDate startDate, LocalDate endDate) {
        DateRange dateRange = new DateRange(startDate, endDate);
        assertFalse(dateRange.hasBusinessDayInRange());
    }

    @ParameterizedTest
    @MethodSource("provideIntersectingRanges")
    void should_detect_intersected_ranges(LocalDate startDate1, LocalDate endDate1, LocalDate startDate2, LocalDate endDate2) {
        DateRange range1 = new DateRange(startDate1, endDate1);
        DateRange range2 = new DateRange(startDate2, endDate2);
        assertAll(
                () -> assertTrue(range1.intersectsWith(range2)),
                () -> assertTrue(range2.intersectsWith(range1))
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonIntersectingRanges")
    void should_detect_non_intersected_ranges(LocalDate startDate1, LocalDate endDate1, LocalDate startDate2, LocalDate endDate2) {
        DateRange range1 = new DateRange(startDate1, endDate1);
        DateRange range2 = new DateRange(startDate2, endDate2);
        assertAll(
                () -> assertFalse(range1.intersectsWith(range2)),
                () -> assertFalse(range2.intersectsWith(range1))
        );
    }

    private static Stream<Arguments> provideCorrectDateRanges() {
        return Stream.of(
                Arguments.of(LocalDate.of(2020, 2, 1), LocalDate.of(2020, 2, 1)),
                Arguments.of(LocalDate.of(2019, 10, 15), LocalDate.of(2020, 10, 17)),
                Arguments.of(LocalDate.of(1900, 1, 1), LocalDate.of(2200, 1, 1))
        );
    }

    private static Stream<Arguments> provideIllegalDateRanges() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(LocalDate.of(2010, 12, 11), null),
                Arguments.of(null, LocalDate.of(2015, 8, 12)),
                Arguments.of(LocalDate.of(2019, 9, 9), LocalDate.of(2019, 9, 8)),
                Arguments.of(LocalDate.of(2200, 1, 1), LocalDate.of(1900, 1, 1))
        );
    }

    private static Stream<Arguments> provideRangeWithBusinessDays() {
        return Stream.of(
                Arguments.of(LocalDate.of(1900, 1, 1), LocalDate.of(2200, 1, 1)),
                Arguments.of(LocalDate.of(2020, 10, 26), LocalDate.of(2020, 10, 26)),
                Arguments.of(LocalDate.of(2020, 10, 28), LocalDate.of(2020, 10, 28)),
                Arguments.of(LocalDate.of(2020, 10, 23), LocalDate.of(2020, 10, 23)),
                Arguments.of(LocalDate.of(2020, 10, 23), LocalDate.of(2020, 10, 28)),
                Arguments.of(LocalDate.of(2020, 12, 31), LocalDate.of(2021, 1, 1))
        );
    }

    private static Stream<Arguments> provideRangeWithoutBusinessDays() {
        return Stream.of(
                Arguments.of(LocalDate.of(2020, 10, 25), LocalDate.of(2020, 10, 25)),
                Arguments.of(LocalDate.of(2020, 10, 24), LocalDate.of(2020, 10, 25)),
                Arguments.of(LocalDate.of(2020, 6, 13), LocalDate.of(2020, 6, 13)),
                Arguments.of(LocalDate.of(2017, 12, 31), LocalDate.of(2017, 12, 31))
        );
    }

    private static Stream<Arguments> provideIntersectingRanges() {
        return Stream.of(
                Arguments.of(
                        LocalDate.of(2020, 10, 25), LocalDate.of(2020, 10, 25),
                        LocalDate.of(2020, 10, 25), LocalDate.of(2020, 10, 25)
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
                        LocalDate.of(2020, 10, 25), LocalDate.of(2020, 10, 25),
                        LocalDate.of(2020, 10, 26), LocalDate.of(2020, 10, 26)
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
}