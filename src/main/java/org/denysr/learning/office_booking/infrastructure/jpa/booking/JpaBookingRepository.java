package org.denysr.learning.office_booking.infrastructure.jpa.booking;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaBookingRepository extends JpaRepository<BookingJpaDto, Integer> {
}
