package org.denysr.learning.office_booking.infrastructure.jpa.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JpaBookingRepository extends JpaRepository<BookingJpaDto, Integer> {
    List<BookingJpaDto> getBookingJpaDtosByStartDateBetweenOrEndDateBetween(
            LocalDate startDateLowerValue,
            LocalDate startDateUpperValue,
            LocalDate endDateLowerValue,
            LocalDate endDateUpperValue
    );
}
