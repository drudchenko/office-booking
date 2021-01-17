package org.denysr.learning.office_booking.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.denysr.learning.office_booking.domain.booking.Booking;
import org.denysr.learning.office_booking.domain.booking.BookingDateRange;
import org.denysr.learning.office_booking.domain.booking.BookingId;
import org.denysr.learning.office_booking.domain.booking.BookingManagement;
import org.denysr.learning.office_booking.domain.booking.BusinessWeek;
import org.denysr.learning.office_booking.domain.user.User;
import org.denysr.learning.office_booking.domain.user.UserEmail;
import org.denysr.learning.office_booking.domain.user.UserId;
import org.denysr.learning.office_booking.domain.user.UserName;
import org.denysr.learning.office_booking.domain.validation.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class OfficeBookingControllerIT {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private BookingManagement bookingManagement;
    private ObjectMapper objectMapper = new ObjectMapper();

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

        Map<String, String> params = new HashMap<>();
        params.put("userId", userId);
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        mvc.perform(post("/office/booking")
                .content(objectMapper.writeValueAsString(params))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId", is(bookingId)));
    }

    @Test
    void whenCreateBooking_givenIncorrectParams_shouldReturnUnprocessableEntity() throws Exception {
        String userId = "6";
        String startDate = "2020-10-09";
        String endDate = "2020-10-08";

        Map<String, String> params = new HashMap<>();
        params.put("userId", userId);
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        mvc.perform(post("/office/booking")
                .content(objectMapper.writeValueAsString(params))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error", is("End date should be after start date")));
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

        Map<String, String> params = new HashMap<>();
        params.put("userId", userId);
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        mvc.perform(post("/office/booking")
                .content(objectMapper.writeValueAsString(params))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("")));
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

        Booking booking1 =new Booking(
                new BookingId(bookingId1),
                new User(new UserId(userId1), new UserEmail(userEmail1), new UserName(userFirstName1, userSecondName1)),
                new BookingDateRange(startDate1, endDate1)
        );
        Booking booking2 = new Booking(
                new BookingId(bookingId2),
                new User(new UserId(userId2), new UserEmail(userEmail2), new UserName(userFirstName2, userSecondName2)),
                new BookingDateRange(startDate2, endDate2)
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
                .andExpect(jsonPath("$.error", is("")));
    }

    @Test
    void whenDeleteBooking_givenCorrectParam_shouldReturnSuccessStatus() throws Exception {
        int bookingId = 10;

        mvc.perform(delete("/office/booking/" + bookingId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDeleteBooking_givenIncorrectParam_shouldReturnNotAcceptableStatus() throws Exception {
        int bookingId = 0;

        mvc.perform(delete("/office/booking/" + bookingId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void whenDeleteBooking_givenNonExistentBookingId_shouldReturnNotAcceptableStatus() throws Exception {
        int bookingId = 6;
        String errorMessage = "entity not found";

        doThrow(new EntityNotFoundException(errorMessage))
                .when(bookingManagement).deleteBooking(new BookingId(bookingId));

        mvc.perform(delete("/office/booking/" + bookingId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(errorMessage)));
    }

    @Test
    void whenDeleteBooking_givenUnexpectedError_shouldNotReturnMessage() throws Exception {
        int bookingId = 6;

        doThrow(new RuntimeException("Spooky error")).when(bookingManagement).deleteBooking(new BookingId(bookingId));

        mvc.perform(delete("/office/booking/" + bookingId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("")));
    }
}