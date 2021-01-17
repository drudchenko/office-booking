package org.denysr.learning.office_booking.infrastructure.jpa.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.denysr.learning.office_booking.infrastructure.jpa.user.UserJpaDto;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingJpaDto {
    private int bookingId;
    private UserJpaDto userDto;
    private LocalDate startDate;
    private LocalDate endDate;
}
