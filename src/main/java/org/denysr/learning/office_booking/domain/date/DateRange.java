package org.denysr.learning.office_booking.domain.date;

import lombok.Value;
import org.springframework.util.Assert;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Value
public class DateRange {
    LocalDate startDate;
    LocalDate endDate;

    public DateRange(LocalDate startDate, LocalDate endDate) {
        Assert.notNull(startDate, "Start date is required");
        Assert.notNull(endDate, "End date is required");
        Assert.isTrue(endDate.isAfter(startDate) || endDate.equals(startDate), "End date should be after start date");
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean intersectsWith(DateRange other) {
        return isDateInRange(other.startDate) || isDateInRange(other.endDate);
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
}