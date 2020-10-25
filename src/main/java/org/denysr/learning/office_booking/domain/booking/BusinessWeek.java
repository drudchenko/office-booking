package org.denysr.learning.office_booking.domain.booking;

import lombok.Value;
import org.denysr.learning.office_booking.domain.date.DateRange;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Value
public class BusinessWeek {
    DateRange dateRange;

    /**
     * We're constructing the week from any
     */
    public BusinessWeek(LocalDate weekDay) {
        dateRange = new DateRange(weekDay.with(DayOfWeek.MONDAY), weekDay.with(DayOfWeek.FRIDAY));
    }

    public LocalDate getBusinessWeekStart() {
        return dateRange.getStartDate();
    }

    public LocalDate getBusinessWeekEnd() {
        return dateRange.getEndDate();
    }
}
