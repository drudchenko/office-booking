package org.denysr.learning.office_booking.domain.booking;

import java.util.List;

public interface BookingRepository {
    // Breaks CQS principle, but looks like having booking id in the response is a good idea
    BookingId saveBooking(Booking booking);
    List<Booking> getBookingsForWeek(BusinessWeek businessWeek);
    void deleteBooking(BookingId bookingId);
}
