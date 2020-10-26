package org.denysr.learning.office_booking.domain.date;

import lombok.Value;
import org.apache.commons.lang3.Validate;
import org.denysr.learning.office_booking.domain.validation.ValidatorWrapper;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Value
public class DateRange {
    LocalDate startDate;
    LocalDate endDate;

    public DateRange(LocalDate startDate, LocalDate endDate) {
        ValidatorWrapper.wrapValidators(
                () -> Validate.notNull(startDate, "Start date is required"),
                () -> Validate.notNull(endDate, "End date is required"),
                () -> Validate.isTrue(!startDate.isAfter(endDate), "End date should be after start date")
        );
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean intersectsWith(DateRange other) {
        return isDateInRange(other.startDate) || isDateInRange(other.endDate) || containsCurrentRange(other);
    }

    public boolean hasBusinessDayInRange() {
        return startDate
                .datesUntil(endDate.plusDays(1))
                .anyMatch(this::isBusinessDay);
    }

    private boolean isBusinessDay(LocalDate date) {
        return date.getDayOfWeek().compareTo(DayOfWeek.FRIDAY) <= 0;
    }

    private boolean isDateInRange(LocalDate dateToTest) {
        return !dateToTest.isBefore(startDate) && !dateToTest.isAfter(endDate);
    }

    private boolean containsCurrentRange(DateRange other) {
        return startDate.isAfter(other.getStartDate()) && endDate.isBefore(other.getEndDate());
    }
}