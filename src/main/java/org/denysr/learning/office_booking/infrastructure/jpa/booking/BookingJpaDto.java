package org.denysr.learning.office_booking.infrastructure.jpa.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.denysr.learning.office_booking.infrastructure.jpa.user.UserJpaDto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Booking")
@Table(indexes = @Index(columnList = "startDate,endDate", name = "date_range"))
public class BookingJpaDto {
    @Id
    @GeneratedValue
    private int bookingId;
    @ManyToOne
    private UserJpaDto userDto;
    private LocalDate startDate;
    private LocalDate endDate;
}
