package org.denysr.learning.office_booking.domain.booking;

import lombok.Value;
import org.apache.commons.lang3.Validate;
import org.denysr.learning.office_booking.domain.validation.ValidatorWrapper;

@Value
public class BookingId implements Comparable<BookingId> {
    int bookingId;

    public BookingId(int bookingId) {
        ValidatorWrapper.wrapValidators(
                () -> Validate.isTrue(bookingId > 0, "Booking id should be above 0.")
        );
        this.bookingId = bookingId;
    }

    @Override
    public int compareTo(BookingId o) {
        return Integer.compare(getBookingId(), o.getBookingId());
    }
}
