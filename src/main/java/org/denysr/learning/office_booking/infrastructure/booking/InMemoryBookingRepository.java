package org.denysr.learning.office_booking.infrastructure.booking;


import org.denysr.learning.office_booking.domain.booking.Booking;
import org.denysr.learning.office_booking.domain.booking.BookingId;
import org.denysr.learning.office_booking.domain.booking.BookingRepository;
import org.denysr.learning.office_booking.domain.booking.BusinessWeek;
import org.denysr.learning.office_booking.domain.validation.EntityNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Repository
final public class InMemoryBookingRepository implements BookingRepository {
    /**
     * Of course there is the performance penalty, ideally some DB should be used instead
     */
    private final ConcurrentMap<BookingId, Booking> bookings = new ConcurrentHashMap<>();

    @Override
    public BookingId saveBooking(Booking booking) {
        if (booking.hasId()) {
            return changeBooking(booking);
        } else {
            return createBooking(booking);
        }
    }

    @Override
    public List<Booking> getBookingsForWeek(BusinessWeek businessWeek) {
        return bookings
                .values()
                .stream()
                .filter(booking -> booking.getBookingDateRange().intersectsWith(businessWeek.getDateRange()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBooking(BookingId bookingId) {
        if (!bookings.containsKey(bookingId)) {
            throw new EntityNotFoundException("Booking with the requested id doesn't exist!");
        }
        bookings.remove(bookingId);
    }

    private BookingId changeBooking(Booking booking) {
        final BookingId bookingId = booking.getBookingId();
        if (!bookings.containsKey(bookingId)) {
            throw new EntityNotFoundException("Attempt to change the nonexistent booking.");
        }
        bookings.put(bookingId, booking);
        return bookingId;
    }

    private synchronized BookingId createBooking(Booking booking) {
        BookingId newBookingId;
        if (bookings.isEmpty()) {
            newBookingId = new BookingId(1);
        } else {
            BookingId maxBookingId = Collections.max(bookings.keySet());
            newBookingId = new BookingId(maxBookingId.getBookingId() + 1);
        }
        bookings.put(newBookingId, booking.withBookingId(newBookingId));
        return newBookingId;
    }
}
