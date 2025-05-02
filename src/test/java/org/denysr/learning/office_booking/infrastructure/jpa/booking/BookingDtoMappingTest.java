package org.denysr.learning.office_booking.infrastructure.jpa.booking;

import org.denysr.learning.office_booking.domain.booking.Booking;
import org.denysr.learning.office_booking.domain.booking.BookingDateRange;
import org.denysr.learning.office_booking.domain.booking.BookingId;
import org.denysr.learning.office_booking.domain.user.User;
import org.denysr.learning.office_booking.domain.user.UserEmail;
import org.denysr.learning.office_booking.domain.user.UserId;
import org.denysr.learning.office_booking.domain.user.UserName;
import org.denysr.learning.office_booking.infrastructure.jpa.user.UserJpaDto;
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
    private final static User USER = new User(
            new UserId(9),
            new UserEmail("doe@gmail.com"),
            new UserName("Johnatan", "Doe")
    );
    private final static UserJpaDto USER_DTO = new UserJpaDto(9, "doe@gmail.com", "Johnatan", "Doe");
    private final static LocalDate START_DATE = LocalDate.of(2020, 1, 12);
    private final static LocalDate END_DATE = LocalDate.of(2020, 1, 20);

    @Autowired
    private ModelMapper modelMapper;

    @Test
    void shouldConvertDomainBookingToJpaDto() {
        Booking booking = Booking.builder()
                .withBookingId(new BookingId(BOOKING_ID))
                .withUser(USER)
                .withBookingDateRange(new BookingDateRange(START_DATE, END_DATE))
                .build();

        BookingJpaDto bookingJpaDto = modelMapper.map(booking, BookingJpaDto.class);

        assertAll(
                () -> assertEquals(BOOKING_ID, bookingJpaDto.getBookingId()),
                () -> assertEquals(USER_DTO, bookingJpaDto.getUserDto()),
                () -> assertEquals(START_DATE, bookingJpaDto.getStartDate()),
                () -> assertEquals(END_DATE, bookingJpaDto.getEndDate())
        );
    }

    @Test
    void shouldConvertJpaDtoBookingToDomainModel() {
        BookingJpaDto bookingJpaDto = new BookingJpaDto(BOOKING_ID, USER_DTO, START_DATE, END_DATE);

        Booking booking = modelMapper.map(bookingJpaDto, Booking.class);

        assertAll(
                () -> assertEquals(BOOKING_ID, booking.bookingId().bookingId()),
                () -> assertEquals(USER, booking.user()),
                () -> assertEquals(START_DATE, booking.bookingDateRange().getStartDate()),
                () -> assertEquals(END_DATE, booking.bookingDateRange().getEndDate())
        );

    }
}