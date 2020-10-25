package org.denysr.learning.office_booking.domain.booking;

import lombok.Data;
import org.springframework.util.Assert;

@Data
public class BookingId implements Comparable<BookingId> {
    private final int bookingId;

    public BookingId(int bookingId) {
        Assert.isTrue(bookingId > 0, "Booking id should be above 0.");
        this.bookingId = bookingId;
    }

    @Override
    public int compareTo(BookingId o) {
        return Integer.compare(getBookingId(), o.getBookingId());
    }
}
