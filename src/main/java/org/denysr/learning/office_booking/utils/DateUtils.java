package org.denysr.learning.office_booking.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DateUtils {
    public static boolean isBusinessDay(LocalDate date) {
        return date.getDayOfWeek().compareTo(DayOfWeek.FRIDAY) <= 0;
    }
}
