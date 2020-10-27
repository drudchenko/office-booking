package org.denysr.learning.office_booking.domain.booking;

import lombok.Data;
import org.apache.commons.lang3.Validate;
import org.denysr.learning.office_booking.domain.user.UserId;
import org.denysr.learning.office_booking.domain.validation.ValidatorWrapper;

import java.util.Objects;

@Data
final public class Booking {
    private final BookingId bookingId;
    private final UserId userId;
    private final BookingDateRange bookingDateRange;

    public Booking(BookingId bookingId, UserId userId, BookingDateRange bookingDateRange) {
        ValidatorWrapper.wrapValidators(
                () -> Validate.notNull(userId),
                () -> Validate.notNull(bookingDateRange)
        );
        this.bookingId = bookingId;
        this.userId = userId;
        this.bookingDateRange = bookingDateRange;
    }

    public boolean hasId() {
        return !Objects.isNull(bookingId);
    }

    public Booking withBookingId(BookingId bookingId) {
        return new Booking(bookingId, userId, bookingDateRange);
    }
}
