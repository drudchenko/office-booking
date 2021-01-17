package org.denysr.learning.office_booking.controllers.mapping;

import org.denysr.learning.office_booking.domain.booking.Booking;
import org.denysr.learning.office_booking.domain.booking.BookingDateRange;
import org.denysr.learning.office_booking.domain.booking.BookingId;
import org.denysr.learning.office_booking.domain.user.User;
import org.denysr.learning.office_booking.domain.user.UserEmail;
import org.denysr.learning.office_booking.domain.user.UserId;
import org.denysr.learning.office_booking.domain.user.UserName;
import org.denysr.learning.office_booking.infrastructure.rest.BookingResponseEntity;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class OfficeBookingMapperTest {
    private final static int CORRECT_BOOKING_ID = 34;
    private final static LocalDate DATE_START = LocalDate.of(2020, 1, 1);
    private final static LocalDate DATE_END = LocalDate.of(2020, 1, 11);
    private final static User USER = new User(
            new UserId(14),
            new UserEmail("pip@email.com"),
            new UserName("johnnie", "english")
    );

    @Autowired
    private ModelMapper modelMapper;

    @Test
    void shouldTransformCorrectDomainBookingToRestDto() {
        Booking booking = new Booking(
                new BookingId(CORRECT_BOOKING_ID),
                USER,
                new BookingDateRange(DATE_START, DATE_END)
        );

        BookingResponseEntity bookingResponseEntity = modelMapper.map(booking, BookingResponseEntity.class);

        assertAll(
                () -> assertEquals(CORRECT_BOOKING_ID, bookingResponseEntity.getBookingId()),
                () -> assertEquals("johnnie english", bookingResponseEntity.getUserName()),
                () -> assertEquals(DATE_START, bookingResponseEntity.getStartDate()),
                () -> assertEquals(DATE_END, bookingResponseEntity.getEndDate())
        );
    }
}
