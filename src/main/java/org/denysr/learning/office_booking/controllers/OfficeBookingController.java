package org.denysr.learning.office_booking.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.denysr.learning.office_booking.domain.booking.Booking;
import org.denysr.learning.office_booking.domain.booking.BookingDateRange;
import org.denysr.learning.office_booking.domain.booking.BookingId;
import org.denysr.learning.office_booking.domain.booking.BookingManagement;
import org.denysr.learning.office_booking.domain.booking.BusinessWeek;
import org.denysr.learning.office_booking.domain.user.UserId;
import org.denysr.learning.office_booking.domain.validation.EntityNotFoundException;
import org.denysr.learning.office_booking.domain.validation.IllegalValueException;
import org.denysr.learning.office_booking.infrastructure.rest.BookingResponseEntity;
import org.denysr.learning.office_booking.infrastructure.rest.ErrorResponse;
import org.denysr.learning.office_booking.infrastructure.rest.OfficeBookingRestDto;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/office")
@RequiredArgsConstructor
@Log4j2
final public class OfficeBookingController {
    private final BookingManagement bookingManagement;
    private final ModelMapper modelMapper;

    @Operation(summary = "Book office", description = "Book office for mentioned user for desired timeframe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created", content = @Content),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid parameter(s) supplied",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Unknown server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/booking", consumes = {"application/json"})
    public ResponseEntity<?> createBooking(
            @RequestBody
            OfficeBookingRestDto officeBookingDto
    ) {
        try {
            BookingId bookingId = bookingManagement.insertBooking(
                    new UserId(officeBookingDto.userId()),
                    new BookingDateRange(officeBookingDto.startDate(), officeBookingDto.endDate())
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of bookings for the week", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = BookingResponseEntity.class))
            )),
            @ApiResponse(responseCode = "500", description = "Unknown server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/bookings/{businessDay}")
    public ResponseEntity<?> getBookings(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Schema(
                    description = "Business day, which represents the week for which we're retrieving bookings",
                    required = true,
                    example = "2020-12-14"
            )
            LocalDate businessDay
    ) {
        try {
            final BusinessWeek businessWeek = new BusinessWeek(businessDay);
            List<Booking> bookings = bookingManagement.fetchAllBookingsForWeek(businessWeek);
            return ResponseEntity.ok().body(
                    bookings.stream()
                            .map(booking -> modelMapper.map(booking, BookingResponseEntity.class))
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Booking with mentioned id not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "406", description = "Invalid parameter supplied",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unknown server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping(value = "/booking/{bookingId}")
    public ResponseEntity<?> deleteBooking(
            @PathVariable
            @Schema(description = "Id of the booking to delete", required = true, example = "20")
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
}
