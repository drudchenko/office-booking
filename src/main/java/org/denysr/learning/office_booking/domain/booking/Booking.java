package org.denysr.learning.office_booking.domain.booking;

import org.denysr.learning.office_booking.domain.user.UserId;
import lombok.Data;
import lombok.NonNull;

import java.util.Objects;

@Data
public class Booking {
    private final BookingId bookingId;
    @NonNull
    private final UserId userId;
    @NonNull
    private final BookingDateRange bookingDateRange;

    public boolean hasId() {
        return !Objects.isNull(bookingId);
    }

    public Booking withBookingId(BookingId bookingId) {
        return new Booking(bookingId, userId, bookingDateRange);
    }
}
