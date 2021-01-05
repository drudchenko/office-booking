package org.denysr.learning.office_booking.infrastructure.jpa.booking;

import org.denysr.learning.office_booking.domain.booking.Booking;
import org.denysr.learning.office_booking.domain.booking.BookingDateRange;
import org.denysr.learning.office_booking.domain.booking.BookingId;
import org.denysr.learning.office_booking.domain.user.UserId;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BookingDtoMappingTest {
    private final static int BOOKING_ID = 27;
    private final static int USER_ID = 9;
    private final static LocalDate START_DATE = LocalDate.of(2020, 1, 12);
    private final static LocalDate END_DATE = LocalDate.of(2020, 1, 20);

    @Autowired
    private ModelMapper modelMapper;

    @Test
    void shouldConvertDomainBookingToJpaDto() {
        Booking booking = Booking.builder()
                .withBookingId(new BookingId(BOOKING_ID))
                .withUserId(new UserId(USER_ID))
                .withBookingDateRange(new BookingDateRange(START_DATE, END_DATE))
                .build();

        BookingJpaDto bookingJpaDto = modelMapper.map(booking, BookingJpaDto.class);

        assertAll(
                () -> assertEquals(BOOKING_ID, bookingJpaDto.getBookingId()),
                () -> assertEquals(USER_ID, bookingJpaDto.getUserId()),
                () -> assertEquals(START_DATE, bookingJpaDto.getStartDate()),
                () -> assertEquals(END_DATE, bookingJpaDto.getEndDate())
        );
    }

    @Test
    void shouldConvertJpaDtoBookingToDomainModel() {
        BookingJpaDto bookingJpaDto = new BookingJpaDto(BOOKING_ID, USER_ID, START_DATE, END_DATE);

        Booking booking = modelMapper.map(bookingJpaDto, Booking.class);

        assertAll(
                () -> assertEquals(BOOKING_ID, booking.getBookingId().getBookingId()),
                () -> assertEquals(USER_ID, booking.getUserId().getUserId()),
                () -> assertEquals(START_DATE, booking.getBookingDateRange().getStartDate()),
                () -> assertEquals(END_DATE, booking.getBookingDateRange().getEndDate())
        );

    }
}