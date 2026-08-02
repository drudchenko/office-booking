package org.denysr.learning.office_booking.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * End-to-end coverage of the basic booking path against the application running in a container.
 * <p>
 * Listing bookings returns everything booked in that business week by anyone, so each test claims a
 * week of its own rather than filtering other tests out of a shared one.
 */
@ExtendWith(ApplicationUnderTest.class)
class BookingApiE2eTest {
    /** A Monday, far enough out that no other test data lands in these weeks. */
    private static final LocalDate FIRST_WEEK_MONDAY = LocalDate.of(2030, 1, 7);
    private static final AtomicInteger WEEK_COUNTER = new AtomicInteger();
    private static final AtomicInteger EMAIL_COUNTER = new AtomicInteger();

    private final BookingApiClient bookings;
    private final UserApiClient users;

    BookingApiE2eTest(BookingApiClient bookings, UserApiClient users) {
        this.bookings = bookings;
        this.users = users;
    }

    @Test
    @DisplayName("A booked office shows up in the bookings of its week")
    void bookedOfficeIsListedForItsWeek() {
        final int userId = registerUser("Lena", "Novak");
        final LocalDate monday = ownWeek();
        final LocalDate friday = monday.with(DayOfWeek.FRIDAY);

        final ApiResponse created = bookings.createBooking(BookingPayload.of(userId, monday, friday));
        assertThat(created.status()).as("create: %s", created).isEqualTo(201);

        final int bookingId = created.bookingId();
        assertThat(bookingId).isPositive();

        final ApiResponse forWeek = bookings.getBookingsForWeekOf(monday);
        assertThat(forWeek.status()).as("list: %s", forWeek).isEqualTo(200);

        final JsonNode listed = findById(forWeek.json(), bookingId);
        assertThat(listed).as("booking %d in %s", bookingId, forWeek.body()).isNotNull();
        assertThat(listed.path("userName").asText()).isEqualTo("Lena Novak");
        assertThat(listed.path("startDate").asText()).isEqualTo(monday.toString());
        assertThat(listed.path("endDate").asText()).isEqualTo(friday.toString());
    }

    @Test
    @DisplayName("A deleted booking disappears from the bookings of its week")
    void deletedBookingIsNotListed() {
        final int userId = registerUser("Marco", "Rossi");
        final LocalDate monday = ownWeek();
        final int bookingId = book(userId, monday, monday.with(DayOfWeek.FRIDAY));

        final ApiResponse deleted = bookings.deleteBooking(bookingId);
        assertThat(deleted.status()).as("delete: %s", deleted).isEqualTo(200);

        final ApiResponse forWeek = bookings.getBookingsForWeekOf(monday);
        assertThat(forWeek.status()).isEqualTo(200);
        assertThat(findById(forWeek.json(), bookingId))
                .as("booking %d should be gone", bookingId)
                .isNull();
    }

    @Test
    @DisplayName("A week with nothing booked lists no bookings")
    void emptyWeekListsNothing() {
        final ApiResponse forWeek = bookings.getBookingsForWeekOf(ownWeek());

        assertThat(forWeek.status()).as("%s", forWeek).isEqualTo(200);
        assertThat(forWeek.json()).isEmpty();
    }

    @Test
    @DisplayName("Booking a range without a business day is rejected")
    void weekendOnlyBookingIsRejected() {
        final int userId = registerUser("Ida", "Berg");
        final LocalDate saturday = ownWeek().plusDays(5);

        final ApiResponse response =
                bookings.createBooking(BookingPayload.of(userId, saturday, saturday.plusDays(1)));

        assertThat(response.status()).as("%s", response).isEqualTo(422);
        assertThat(response.error()).isEqualTo("There should be at least one business day in the range");
    }

    @Test
    @DisplayName("Booking a range that ends before it starts is rejected")
    void invertedRangeIsRejected() {
        final int userId = registerUser("Petr", "Novy");
        final LocalDate monday = ownWeek();

        final ApiResponse response =
                bookings.createBooking(BookingPayload.of(userId, monday.with(DayOfWeek.FRIDAY), monday));

        assertThat(response.status()).as("%s", response).isEqualTo(422);
        assertThat(response.error()).isEqualTo("End date should be after start date");
    }

    @Test
    @DisplayName("Deleting a booking with a non-positive id is rejected")
    void nonPositiveBookingIdIsRejected() {
        final ApiResponse response = bookings.deleteBooking(0);

        assertThat(response.status()).as("%s", response).isEqualTo(406);
        assertThat(response.error()).isEqualTo("Booking id should be above 0.");
    }

    @Test
    @DisplayName("Booking for an unknown user reports a client error")
    void bookingForUnknownUserIsRejected() {
        final LocalDate monday = ownWeek();

        final ApiResponse response = bookings.createBooking(
                BookingPayload.of(Integer.MAX_VALUE, monday, monday.with(DayOfWeek.FRIDAY)));

        assertThat(response.status()).as("%s", response).isBetween(400, 499);
    }

    @Test
    @DisplayName("Deleting an unknown booking reports not found")
    void deletingUnknownBookingReportsNotFound() {
        final ApiResponse response = bookings.deleteBooking(Integer.MAX_VALUE);

        assertThat(response.status()).as("%s", response).isEqualTo(404);
    }

    private int registerUser(String firstName, String secondName) {
        final String email = "e2e.booker." + EMAIL_COUNTER.incrementAndGet() + "@example.com";
        final ApiResponse created = users.createUser(new UserPayload(email, firstName, secondName));
        assertThat(created.status()).as("create user: %s", created).isEqualTo(201);
        return created.userId();
    }

    private int book(int userId, LocalDate startDate, LocalDate endDate) {
        final ApiResponse created = bookings.createBooking(BookingPayload.of(userId, startDate, endDate));
        assertThat(created.status()).as("create booking: %s", created).isEqualTo(201);
        return created.bookingId();
    }

    /** A business week no other test touches, so listings only ever show this test's bookings. */
    private static LocalDate ownWeek() {
        return FIRST_WEEK_MONDAY.plusWeeks(WEEK_COUNTER.getAndIncrement());
    }

    private static JsonNode findById(JsonNode bookings, int bookingId) {
        for (JsonNode booking : bookings) {
            if (booking.path("bookingId").asInt() == bookingId) {
                return booking;
            }
        }
        return null;
    }
}
