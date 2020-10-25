package org.denysr.learning.office_booking.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.denysr.learning.office_booking.domain.booking.*;
import org.denysr.learning.office_booking.domain.date.DateRange;
import org.denysr.learning.office_booking.domain.user.User;
import org.denysr.learning.office_booking.domain.user.UserId;
import org.denysr.learning.office_booking.infrastructure.rest.BookingResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/office")
@RequiredArgsConstructor
@Log4j2
public class OfficeBookingController {
    private final BookingManagement bookingManagement;

    @PostMapping("/booking")
    public ResponseEntity<?> createBooking(
            @RequestParam
            int userId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        try {
            BookingId bookingId = bookingManagement.insertBooking(
                    new UserId(userId),
                    new BookingDateRange(startDate, endDate)
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(bookingId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error processing booking creation", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/bookings/{businessDay}")
    public ResponseEntity<?> getBookings(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate businessDay
    ) {
        try {
            final BusinessWeek businessWeek = new BusinessWeek(businessDay);
            List<Pair<Booking, User>> bookings = bookingManagement.fetchAllBookingsForWeek(businessWeek);
            return ResponseEntity.ok().body(
                    bookings.stream()
                            .map(booking -> bookingToResponseEntity(booking, businessWeek))
                            .collect(Collectors.toList())
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error during fetching bookings", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/booking")
    public ResponseEntity<?> deleteBooking(
            @RequestParam
            int bookingId
    ) {
        try {
            bookingManagement.deleteBooking(new BookingId(bookingId));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking with mentioned id not found");
        } catch (Exception e) {
            log.error("Error processing booking deletion", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private BookingResponseEntity bookingToResponseEntity(Pair<Booking, User> booking, BusinessWeek businessWeek) {
        final DateRange timeframeForWeek = booking.getLeft().getBookingDateRange().getRangeForWeek(businessWeek);
        return new BookingResponseEntity(
                booking.getLeft().getBookingId().getBookingId(),
                booking.getRight().getUserName().getFullName(),
                timeframeForWeek.getStartDate(),
                timeframeForWeek.getEndDate()
        );
    }
}
