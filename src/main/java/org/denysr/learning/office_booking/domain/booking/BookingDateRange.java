package org.denysr.learning.office_booking.domain.booking;

import org.apache.commons.lang3.Validate;
import org.denysr.learning.office_booking.domain.date.DateRange;
import org.denysr.learning.office_booking.domain.validation.ValidatorWrapper;

import java.time.LocalDate;

public record BookingDateRange(DateRange dateRange) {

    public BookingDateRange{
        ValidatorWrapper.wrapValidators(
                () -> Validate.isTrue(
                        dateRange.hasBusinessDayInRange(),
                        "There should be at least one business day in the range"
                )
        );
    }

    public BookingDateRange(LocalDate startDate, LocalDate endDate) {
        this(new DateRange(startDate, endDate));
    }

    /**
     * Checks if there is any intersection between 2 timeframes.
     * Could be used to ban bookings with intersecting timeframes
     */
    public boolean intersectsWith(DateRange dateRangeParam) {
        return dateRange.intersectsWith(dateRangeParam);
    }

    public LocalDate getStartDate() {
        return dateRange.startDate();
    }

    public LocalDate getEndDate() {
        return dateRange.endDate();
    }

    public DateRange getRangeForWeek(BusinessWeek businessWeek) {
        ValidatorWrapper.wrapValidators(
                () -> Validate.isTrue(
                        intersectsWith(businessWeek.dateRange()),
                        "The timeframe has no intersection with the week"
                )
        );
        LocalDate weekRangeStart = businessWeek.getBusinessWeekStart().isAfter(getStartDate()) ?
                businessWeek.getBusinessWeekStart() : getStartDate();
        LocalDate weekRangeEnd = businessWeek.getBusinessWeekEnd().isBefore(getEndDate()) ?
                businessWeek.getBusinessWeekEnd() : getEndDate();
        return new DateRange(weekRangeStart, weekRangeEnd);
    }
}
