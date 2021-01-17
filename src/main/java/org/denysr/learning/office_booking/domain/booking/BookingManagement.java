package org.denysr.learning.office_booking.domain.booking;

import lombok.RequiredArgsConstructor;
import org.denysr.learning.office_booking.domain.date.DateRange;
import org.denysr.learning.office_booking.domain.user.User;
import org.denysr.learning.office_booking.domain.user.UserId;
import org.denysr.learning.office_booking.domain.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
final public class BookingManagement {
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public BookingId insertBooking(UserId userId, BookingDateRange bookingDateRange) {
        User user = userRepository.findUserById(userId);
        Assert.notNull(user, "User with mentioned id doesn't exist");
        // Additional validation could be provided here, like check for timeframe intersections for particular user etc
        // Should be implemented after communicating to domain expert/product owner.
        Booking booking = new Booking(null, user, bookingDateRange);
        return bookingRepository.saveBooking(booking);
    }

    public void deleteBooking(BookingId bookingId) {
        bookingRepository.deleteBooking(bookingId);
    }

    public List<Booking> fetchAllBookingsForWeek(BusinessWeek businessWeek) {
        return bookingRepository.getBookingsForWeek(businessWeek)
                .stream()
                .map(booking -> withWeek(booking, businessWeek))
                .collect(Collectors.toList());
    }

    private Booking withWeek(Booking booking, BusinessWeek businessWeek) {
        final DateRange weekRange = booking.getBookingDateRange().getRangeForWeek(businessWeek);
        return booking.toBuilder()
                .withBookingDateRange(new BookingDateRange(weekRange))
                .build();
    }
}
