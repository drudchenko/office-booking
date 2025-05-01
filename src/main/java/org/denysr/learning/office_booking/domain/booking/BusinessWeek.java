package org.denysr.learning.office_booking.domain.booking;

import org.apache.commons.lang3.Validate;
import org.denysr.learning.office_booking.domain.date.DateRange;
import org.denysr.learning.office_booking.domain.validation.ValidatorWrapper;

import java.time.DayOfWeek;
import java.time.LocalDate;

public record BusinessWeek(DateRange dateRange) {

    public BusinessWeek {
        ValidatorWrapper.wrapValidators(
            () -> Validate.isTrue(dateRange.startDate().getDayOfWeek() == DayOfWeek.MONDAY),
            () -> Validate.isTrue(dateRange.endDate().getDayOfWeek() == DayOfWeek.FRIDAY),
            () -> Validate.isTrue(dateRange.getBusinessDaysCount() == 5)
        );
    }

    /**
     * We're constructing the week from any day
     */
    public BusinessWeek(LocalDate weekDay) {
        this(new DateRange(weekDay.with(DayOfWeek.MONDAY), weekDay.with(DayOfWeek.FRIDAY)));
    }

    public LocalDate getBusinessWeekStart() {
        return dateRange.startDate();
    }

    public LocalDate getBusinessWeekEnd() {
        return dateRange.endDate();
    }
}
