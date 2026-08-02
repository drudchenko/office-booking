package org.denysr.learning.office_booking.infrastructure.jpa.booking;

import lombok.RequiredArgsConstructor;
import org.denysr.learning.office_booking.domain.booking.Booking;
import org.denysr.learning.office_booking.domain.booking.BookingId;
import org.denysr.learning.office_booking.domain.booking.BookingRepository;
import org.denysr.learning.office_booking.domain.booking.BusinessWeek;
import org.denysr.learning.office_booking.domain.validation.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class BookingRepositoryJpaImpl implements BookingRepository {
    private final JpaBookingRepository jpaBookingRepository;
    private final ModelMapper modelMapper;

    @Override
    public BookingId saveBooking(Booking booking) throws EntityNotFoundException {
        final int bookingId = jpaBookingRepository
                .save(modelMapper.map(booking, BookingJpaDto.class))
                .getBookingId();
        return new BookingId(bookingId);
    }

    @Override
    public List<Booking> getBookingsForWeek(BusinessWeek businessWeek) {
        return modelMapper.map(jpaBookingRepository.getBookingJpaDtosByStartDateBetweenOrEndDateBetween(
                        businessWeek.getBusinessWeekStart(), businessWeek.getBusinessWeekEnd(),
                        businessWeek.getBusinessWeekStart(), businessWeek.getBusinessWeekEnd()
                ), new TypeToken<List<Booking>>() {}.getType());
    }

    /** {@code deleteById} is a no-op for unknown ids, so the row has to be looked up first. */
    @Override
    public void deleteBooking(BookingId bookingId) throws EntityNotFoundException {
        if (!jpaBookingRepository.existsById(bookingId.bookingId())) {
            throw new EntityNotFoundException("Booking with id " + bookingId.bookingId() + " not found");
        }
        jpaBookingRepository.deleteById(bookingId.bookingId());
    }
}
