package org.denysr.learning.office_booking.domain.booking;

import lombok.Value;
import org.apache.commons.lang3.Validate;
import org.denysr.learning.office_booking.domain.date.DateRange;
import org.denysr.learning.office_booking.domain.validation.ValidatorWrapper;

import java.time.LocalDate;

@Value
public class BookingDateRange {
    DateRange dateRange;

    public BookingDateRange(LocalDate startDate, LocalDate endDate) {
        dateRange = new DateRange(startDate, endDate);
        ValidatorWrapper.wrapValidators(
                () -> Validate.isTrue(
                        dateRange.hasBusinessDayInRange(),
                        "There should be at least one business day in the range"
                )
        );
    }

    /**
     * Checks if there is any intersection between 2 timeframes.
     * Could be used to ban bookings with intersecting timeframes
     */
    public boolean intersectsWith(DateRange dateRangeParam) {
        return dateRange.intersectsWith(dateRangeParam);
    }

    public LocalDate getStartDate() {
        return dateRange.getStartDate();
    }

    public LocalDate getEndDate() {
        return dateRange.getEndDate();
    }

    public DateRange getRangeForWeek(BusinessWeek businessWeek) {
        Validate.isTrue(intersectsWith(businessWeek.getDateRange()), "The timeframe has no intersection with the week");
        LocalDate weekRangeStart = businessWeek.getBusinessWeekStart().isAfter(getStartDate()) ?
                businessWeek.getBusinessWeekStart() : getStartDate();
        LocalDate weekRangeEnd = businessWeek.getBusinessWeekEnd().isBefore(getEndDate()) ?
                businessWeek.getBusinessWeekEnd() : getEndDate();
        return new DateRange(weekRangeStart, weekRangeEnd);
    }
}
