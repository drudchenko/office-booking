package org.denysr.learning.office_booking.infrastructure.jpa.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingJpaDto {
    private int bookingId;
    private int userId;
    private LocalDate startDate;
    private LocalDate endDate;
}
