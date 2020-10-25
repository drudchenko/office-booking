package org.denysr.learning.office_booking.domain.booking;

import org.denysr.learning.office_booking.utils.DateUtils;
import lombok.Data;
import org.springframework.util.Assert;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Data
public class Week {
    private final LocalDate weekStart;
    private final LocalDate weekEnd;

    /**
     * We're constructing the week from any
     */
    public Week(LocalDate weekDay) {
        Assert.isTrue(DateUtils.isBusinessDay(weekDay), "Only business day is allowed.");
        weekStart = weekDay.with(DayOfWeek.MONDAY);
        weekEnd = weekDay.with(DayOfWeek.FRIDAY);
    }
}
