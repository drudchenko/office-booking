package org.denysr.learning.office_booking.domain.booking;

import org.apache.commons.lang3.Validate;
import org.denysr.learning.office_booking.domain.validation.ValidatorWrapper;

public record BookingId(int bookingId) implements Comparable<BookingId> {
    public BookingId(int bookingId) {
        ValidatorWrapper.wrapValidators(
                () -> Validate.isTrue(bookingId > 0, "Booking id should be above 0.")
        );
        this.bookingId = bookingId;
    }

    @Override
    public int compareTo(BookingId o) {
        return Integer.compare(bookingId(), o.bookingId());
    }
}
