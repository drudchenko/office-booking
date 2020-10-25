package org.denysr.learning.office_booking.domain.booking;

import org.denysr.learning.office_booking.utils.DateUtils;
import lombok.Data;
import org.springframework.util.Assert;

import java.time.LocalDate;

@Data
public class BookingTimeframe {
    private final LocalDate startDate;
    private final LocalDate endDate;

    public BookingTimeframe(LocalDate startDate, LocalDate endDate) {
        Assert.notNull(startDate, "Start date is required");
        Assert.notNull(endDate, "End date is required");
        Assert.isTrue(endDate.isAfter(startDate) || endDate.equals(startDate), "End date should be after start date");
        Assert.isTrue(
                hasBusinessDayInRange(startDate, endDate),
                "There should be at least one business day in the range"
        );
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Checks if there is any intersection between 2 timeframes.
     * Could be used to ban bookings with intersecting timeframes
     */
    public boolean intersectsWith(BookingTimeframe other) {
        return isDateInRange(other.startDate) || isDateInRange(other.endDate);
    }

    public boolean intersectsWith(Week week) {
        return isDateInRange(week.getWeekStart()) || isDateInRange(week.getWeekEnd());
    }

    public BookingTimeframe getRangeForWeek(Week week) {
        Assert.isTrue(intersectsWith(week), "The timeframe has no intersection with the week");
        LocalDate weekRangeStart = week.getWeekStart().isAfter(startDate) ? week.getWeekStart() : startDate;
        LocalDate weekRangeEnd = week.getWeekEnd().isBefore(endDate) ? week.getWeekEnd() : endDate;
        return new BookingTimeframe(weekRangeStart, weekRangeEnd);
    }

    private boolean hasBusinessDayInRange(LocalDate startDate, LocalDate endDate) {
        return startDate
                .datesUntil(endDate.plusDays(1))
                .anyMatch(DateUtils::isBusinessDay);
    }

    private boolean isDateInRange(LocalDate dateToTest) {
        return !dateToTest.isBefore(startDate) && !dateToTest.isAfter(endDate);
    }
}
