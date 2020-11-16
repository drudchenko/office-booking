package org.denysr.learning.office_booking.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.denysr.learning.office_booking.domain.booking.*;
import org.denysr.learning.office_booking.domain.date.DateRange;
import org.denysr.learning.office_booking.domain.user.User;
import org.denysr.learning.office_booking.domain.user.UserId;
import org.denysr.learning.office_booking.domain.validation.EntityNotFoundException;
import org.denysr.learning.office_booking.domain.validation.IllegalValueException;
import org.denysr.learning.office_booking.infrastructure.rest.BookingResponseEntity;
import org.denysr.learning.office_booking.infrastructure.rest.ErrorResponse;
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
final public class OfficeBookingController {
    private final BookingManagement bookingManagement;

    @Operation(summary = "Book office", description = "Book office for mentioned user for desired timeframe")
    @PostMapping(value = "/booking", consumes = {"application/json"})
    public ResponseEntity<?> createBooking(
            @Parameter(description = "User id", required = true, example = "3")
            int userId,
            @Parameter(description = "Booking start date", required = true, example = "2020-01-01")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @Parameter(description = "Booking end date", required = true, example = "2020-01-06")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        try {
            BookingId bookingId = bookingManagement.insertBooking(
                    new UserId(userId),
                    new BookingDateRange(startDate, endDate)
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(bookingId);
        } catch (IllegalValueException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error processing booking creation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(""));
        }
    }

    @Operation(summary = "Get office bookings for the week",
            description = "Get office bookings for the week, defined by the day of the input parameter")
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
        } catch (IllegalValueException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error during fetching bookings", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(""));
        }
    }

    @Operation(summary = "Delete booking", description = "Delete booking by id")
    @DeleteMapping(value = "/booking", consumes = {"application/json"})
    public ResponseEntity<?> deleteBooking(
            @Parameter(description = "Booking id", required = true, example = "3")
            int bookingId
    ) {
        try {
            bookingManagement.deleteBooking(new BookingId(bookingId));
            return ResponseEntity.ok().build();
        } catch (IllegalValueException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorResponse(e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error processing booking deletion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(""));
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
