package org.denysr.learning.office_booking.controllers;

import org.apache.commons.lang3.tuple.Pair;
import org.denysr.learning.office_booking.domain.booking.*;
import org.denysr.learning.office_booking.domain.user.User;
import org.denysr.learning.office_booking.domain.user.UserEmail;
import org.denysr.learning.office_booking.domain.user.UserId;
import org.denysr.learning.office_booking.domain.user.UserName;
import org.denysr.learning.office_booking.domain.validation.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@WebMvcTest(OfficeBookingController.class)
class OfficeBookingControllerIT {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private BookingManagement bookingManagement;

    @Test
    void whenCreateBooking_givenCorrectParams_shouldReturnBookingId() throws Exception {
        int bookingId = 90;
        String userId = "6";
        String startDate = "2020-10-09";
        String endDate = "2020-10-09";
        LocalDate localDate = LocalDate.of(2020, 10, 9);

        UserId expectedUserId = new UserId(Integer.parseInt(userId));
        BookingDateRange expectedBookingDateRange = new BookingDateRange(localDate, localDate);
        when(bookingManagement.insertBooking(expectedUserId, expectedBookingDateRange))
                .thenReturn(new BookingId(bookingId));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("userId", userId);
        params.add("startDate", startDate);
        params.add("endDate", endDate);

        mvc.perform(post("/office/booking")
                .params(params)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId", is(bookingId)));
    }

    @Test
    void whenCreateBooking_givenIncorrectParams_shouldReturnUnprocessableEntity() throws Exception {
        String userId = "6";
        String startDate = "2020-10-09";
        String endDate = "2020-10-08";
        LocalDate localDate = LocalDate.of(2020, 10, 9);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("userId", userId);
        params.add("startDate", startDate);
        params.add("endDate", endDate);

        mvc.perform(post("/office/booking")
                .params(params)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("End date should be after start date"));
    }

    @Test
    void whenCreateBooking_givenUnexpectedError_shouldNotReturnMessage() throws Exception {
        String userId = "6";
        String startDate = "2020-10-09";
        String endDate = "2020-10-09";
        LocalDate localDate = LocalDate.of(2020, 10, 9);

        UserId expectedUserId = new UserId(Integer.parseInt(userId));
        BookingDateRange expectedBookingDateRange = new BookingDateRange(localDate, localDate);
        when(bookingManagement.insertBooking(expectedUserId, expectedBookingDateRange))
                .thenThrow(new RuntimeException("some error message"));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("userId", userId);
        params.add("startDate", startDate);
        params.add("endDate", endDate);

        mvc.perform(post("/office/booking")
                .params(params)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(""));
    }

    @Test
    void whenGetBookings_givenCorrectParams_shouldReturnBookings() throws Exception {
        LocalDate businessDay = LocalDate.of(2020, 11, 5);
        String businessDayParam = businessDay.format(DateTimeFormatter.ISO_DATE);

        int bookingId1 = 5;
        int userId1 = 1;
        LocalDate startDate1 = LocalDate.of(2020, 11, 5);
        LocalDate endDate1 = LocalDate.of(2020, 11, 6);
        String userEmail1 = "pupkin@mail.by";
        String userFirstName1 = "Vasia";
        String userSecondName1 = "Pupkin";
        int bookingId2 = 10;
        int userId2 = 6;
        LocalDate startDate2 = LocalDate.of(2020, 11, 3);
        LocalDate endDate2 = LocalDate.of(2020, 11, 5);
        String userEmail2 = "John.doe@live.com";
        String userFirstName2 = "John";
        String userSecondName2 = "Doe";

        Pair<Booking, User> booking1 = Pair.of(
                new Booking(new BookingId(bookingId1), new UserId(userId1), new BookingDateRange(startDate1, endDate1)),
                new User(new UserId(userId1), new UserEmail(userEmail1), new UserName(userFirstName1, userSecondName1))
        );
        Pair<Booking, User> booking2 = Pair.of(
                new Booking(new BookingId(bookingId2), new UserId(userId2), new BookingDateRange(startDate2, endDate2)),
                new User(new UserId(userId2), new UserEmail(userEmail2), new UserName(userFirstName2, userSecondName2))
        );

        when(bookingManagement.fetchAllBookingsForWeek(new BusinessWeek(businessDay)))
                .thenReturn(Arrays.asList(booking1, booking2));

        mvc.perform(get("/office/bookings/" + businessDayParam)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].bookingId", is(bookingId1)))
                .andExpect(jsonPath("$[0].userName", is(userFirstName1 + " " + userSecondName1)))
                .andExpect(jsonPath("$[0].startDate", is(startDate1.format(DateTimeFormatter.ISO_DATE))))
                .andExpect(jsonPath("$[0].endDate", is(endDate1.format(DateTimeFormatter.ISO_DATE))))
                .andExpect(jsonPath("$[1].bookingId", is(bookingId2)))
                .andExpect(jsonPath("$[1].userName", is(userFirstName2 + " " + userSecondName2)))
                .andExpect(jsonPath("$[1].startDate", is(startDate2.format(DateTimeFormatter.ISO_DATE))))
                .andExpect(jsonPath("$[1].endDate", is(endDate2.format(DateTimeFormatter.ISO_DATE))));
    }

    @Test
    void whenGetBookings_givenUnexpectedError_shouldNotReturnMessage() throws Exception {
        LocalDate businessDay = LocalDate.of(2020, 11, 5);
        String businessDayParam = businessDay.format(DateTimeFormatter.ISO_DATE);

        when(bookingManagement.fetchAllBookingsForWeek(new BusinessWeek(businessDay)))
                .thenThrow(new RuntimeException("A critical error!"));

        mvc.perform(get("/office/bookings/" + businessDayParam)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(""));
    }

    @Test
    void whenDeleteBooking_givenCorrectParam_shouldReturnSuccessStatus() throws Exception {
        int bookingId = 10;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("bookingId", Integer.toString(bookingId));

        mvc.perform(delete("/office/booking")
                .params(params)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDeleteBooking_givenIncorrectParam_shouldReturnNotAcceptableStatus() throws Exception {
        int bookingId = 0;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("bookingId", Integer.toString(bookingId));

        mvc.perform(delete("/office/booking")
                .params(params)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void whenDeleteBooking_givenNonExistentBookingId_shouldReturnNotAcceptableStatus() throws Exception {
        int bookingId = 6;
        String errorMessage = "entity not found";

        doThrow(new EntityNotFoundException(errorMessage))
                .when(bookingManagement).deleteBooking(new BookingId(bookingId));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("bookingId", Integer.toString(bookingId));

        mvc.perform(delete("/office/booking")
                .params(params)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(errorMessage));
    }

    @Test
    void whenDeleteBooking_givenUnexpectedError_shouldNotReturnMessage() throws Exception {
        int bookingId = 6;

        doThrow(new RuntimeException("Spooky error")).when(bookingManagement).deleteBooking(new BookingId(bookingId));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("bookingId", Integer.toString(bookingId));

        mvc.perform(delete("/office/booking")
                .params(params)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(""));
    }
}