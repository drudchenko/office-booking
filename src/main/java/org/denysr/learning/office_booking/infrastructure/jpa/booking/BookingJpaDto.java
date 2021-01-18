package org.denysr.learning.office_booking.infrastructure.jpa.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.denysr.learning.office_booking.infrastructure.jpa.user.UserJpaDto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
