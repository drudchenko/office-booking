package org.denysr.learning.office_booking.domain.booking;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.Validate;
import org.denysr.learning.office_booking.domain.user.User;
import org.denysr.learning.office_booking.domain.validation.ValidatorWrapper;

import java.util.Objects;

@Data
@Builder(toBuilder = true, setterPrefix = "with")
final public class Booking {
    private final BookingId bookingId;
    private final User user;
    private final BookingDateRange bookingDateRange;

    public Booking(BookingId bookingId, User user, BookingDateRange bookingDateRange) {
        ValidatorWrapper.wrapValidators(
                () -> Validate.notNull(user),
                () -> Validate.notNull(bookingDateRange)
        );
        this.bookingId = bookingId;
        this.user = user;
        this.bookingDateRange = bookingDateRange;
    }

    public boolean hasId() {
        return !Objects.isNull(bookingId);
    }
}
