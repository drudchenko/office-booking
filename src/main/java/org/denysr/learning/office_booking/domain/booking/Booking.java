package org.denysr.learning.office_booking.domain.booking;

import lombok.Builder;
import org.denysr.learning.office_booking.domain.user.User;
import org.denysr.learning.office_booking.domain.validation.ValidatorWrapper;

import java.util.Objects;

@Builder(toBuilder = true, setterPrefix = "with")
public record Booking (BookingId bookingId, User user, BookingDateRange bookingDateRange) {

    public Booking {
        ValidatorWrapper.wrapValidators(
                () -> Objects.requireNonNull(user),
                () -> Objects.requireNonNull(bookingDateRange)
        );
    }

    public boolean hasId() {
        return !Objects.isNull(bookingId);
    }
}
