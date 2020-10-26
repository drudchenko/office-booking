package org.denysr.learning.office_booking.domain.booking;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BusinessWeekTest {

    @ParameterizedTest
    @MethodSource("provideTestData")
    void should_create_week_for_given_day(LocalDate parameter, LocalDate startDate, LocalDate endDate) {
        BusinessWeek businessWeek = new BusinessWeek(parameter);
        assertAll(
                () -> assertEquals(startDate, businessWeek.getBusinessWeekStart()),
                () -> assertEquals(endDate, businessWeek.getBusinessWeekEnd())
        );
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                // Monday
                Arguments.of(LocalDate.of(2020, 10, 26), LocalDate.of(2020, 10, 26), LocalDate.of(2020, 10, 30)),
                // Tuesday, year end
                Arguments.of(LocalDate.of(2019, 12, 31), LocalDate.of(2019, 12, 30), LocalDate.of(2020, 1, 3)),
                // Wednesday
                Arguments.of(LocalDate.of(2019, 7, 31), LocalDate.of(2019, 7, 29), LocalDate.of(2019, 8, 2)),
                // Wednesday, year beginning
                Arguments.of(LocalDate.of(2020, 1, 1), LocalDate.of(2019, 12, 30), LocalDate.of(2020, 1, 3)),
                // Friday
                Arguments.of(LocalDate.of(1999, 5, 14), LocalDate.of(1999, 5, 10), LocalDate.of(1999, 5, 14)),
                // Saturday
                Arguments.of(LocalDate.of(2019, 8, 31), LocalDate.of(2019, 8, 26), LocalDate.of(2019, 8, 30)),
                // Sunday
                Arguments.of(LocalDate.of(2040, 6, 17), LocalDate.of(2040, 6, 11), LocalDate.of(2040, 6, 15))
        );
    }
}