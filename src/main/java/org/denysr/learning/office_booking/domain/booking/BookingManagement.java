package org.denysr.learning.office_booking.domain.booking;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.denysr.learning.office_booking.domain.user.User;
import org.denysr.learning.office_booking.domain.user.UserId;
import org.denysr.learning.office_booking.domain.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingManagement {
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public BookingId insertBooking(UserId userId, BookingDateRange bookingDateRange) {
        Assert.notNull(userRepository.findUserById(userId), "User with mentioned id doesn't exist");
        // Additional validation could be provided here, like check for timeframe intersections for particular user etc
        // Should be implemented after communicating to domain expert/product owner.
        Booking booking = new Booking(null, userId, bookingDateRange);
        return bookingRepository.saveBooking(booking);
    }

    public void deleteBooking(BookingId bookingId) {
        bookingRepository.deleteBooking(bookingId);
    }

    public List<Pair<Booking, User>> fetchAllBookingsForWeek(BusinessWeek businessWeek) {
        return bookingRepository.getBookingsForWeek(businessWeek)
                .stream()
                .map(this::fetchUserForBooking)
                .collect(Collectors.toList());
    }

    private Pair<Booking, User> fetchUserForBooking(Booking booking) {
        return new ImmutablePair<>(booking, userRepository.findUserById(booking.getUserId()));
    }
}
