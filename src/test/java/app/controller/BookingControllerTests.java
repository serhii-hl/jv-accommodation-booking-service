package app.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.dto.booking.BookingDto;
import app.dto.booking.CreateBookingDto;
import app.model.BookingStatus;
import app.util.BookingUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BookingControllerTests {

    private static final Pageable PAGEABLE = PageRequest.of(0, 10);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(@Autowired WebApplicationContext context) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Successfully return all user`s bookings")
    @WithUserDetails("owner@example.com")
    @Sql(scripts = "classpath:database/booking/add-booking-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/booking/remove-booking-from-test-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAllUserBookings() throws Exception {
        BookingDto expected = BookingUtils.createExpectedBookingDto();
        List<BookingDto> expectedContent = List.of(expected);
        MvcResult result = mockMvc.perform(get("/bookings/my")
                .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                .param("size", String.valueOf(PAGEABLE.getPageSize()))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        List<BookingDto> actualContent = objectMapper.convertValue(
                rootNode.get("content"), new TypeReference<List<BookingDto>>() {}
        );
        long actualTotalElements = rootNode.get("totalElements").asLong();
        assertThat(actualContent).isNotNull().hasSize(expectedContent.size());
        assertThat(actualTotalElements).isEqualTo(expectedContent.size());
        assertThat(actualContent)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .isEqualTo(expectedContent);
        assertThat(actualContent.get(0).getId()).isEqualTo(expected.getId());
    }

    @Test
    @DisplayName("Successfully return booking by id for Admin")
    @WithUserDetails("admin@example.com")
    @Sql(scripts = "classpath:database/booking/add-bookings-to-test-admin-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/booking/remove-bookings-to-test-admin-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBookingByIdAdmin() throws Exception {
        Long bookingId = 100L;
        BookingDto expected = BookingUtils.createExpectedBookingDto();
        MvcResult result = mockMvc.perform(get("/bookings/{id}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        BookingDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookingDto.class);
        assertThat(actualDto).isNotNull();
        assertThat(actualDto)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Get booking by id fails for unauthorized user")
    @WithUserDetails("owner@example.com")
    @Sql(scripts = "classpath:database/booking/add-bookings-to-test-admin-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/booking/remove-bookings-to-test-admin-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBookingByIdFails() throws Exception {
        Long bookingId = 100L;
        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Successfully return filtered bookings by id for Admin")
    @WithUserDetails("admin@example.com")
    @Sql(scripts = "classpath:database/booking/add-bookings-to-test-admin-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/booking/remove-bookings-to-test-admin-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getFilteredBookingsAdmin() throws Exception {
        BookingDto expected = BookingUtils.createExpectedBookingDto();
        List<BookingDto> expectedContent = List.of(expected);
        MvcResult result = mockMvc.perform(get("/bookings/my")
                        .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                        .param("size", String.valueOf(PAGEABLE.getPageSize()))
                        .param("userId", "100")
                        .param("status", "CONFIRMED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        List<BookingDto> actualContent = objectMapper.convertValue(
                rootNode.get("content"), new TypeReference<List<BookingDto>>() {}
        );
        long actualTotalElements = rootNode.get("totalElements").asLong();
        assertThat(actualContent).isNotNull().hasSize(expectedContent.size());
        assertThat(actualTotalElements).isEqualTo(expectedContent.size());
        assertThat(actualContent)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .isEqualTo(expectedContent);
        assertThat(actualContent.get(0).getId()).isEqualTo(expected.getId());
    }

    @Test
    @DisplayName("Successfully return filtered bookings for owner")
    @WithUserDetails("owner@example.com")
    @Sql(scripts = "classpath:database/booking/add-booking-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/booking/remove-booking-from-test-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getFilteredBookingsOwner() throws Exception {
        BookingDto expected = BookingUtils.createExpectedBookingDto();
        List<BookingDto> expectedContent = List.of(expected);

        MvcResult result = mockMvc.perform(get("/bookings/owner")
                        .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                        .param("size", String.valueOf(PAGEABLE.getPageSize()))
                        .param("userId", "100")
                        .param("status", "CONFIRMED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        List<BookingDto> actualContent = objectMapper.convertValue(
                rootNode.get("content"), new TypeReference<List<BookingDto>>() {}
        );
        long actualTotalElements = rootNode.get("totalElements").asLong();

        assertThat(actualContent).isNotNull().hasSize(expectedContent.size());
        assertThat(actualTotalElements).isEqualTo(expectedContent.size());
        assertThat(actualContent)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .isEqualTo(expectedContent);
    }

    @Test
    @DisplayName("Successfully delete booking by id for admin")
    @WithUserDetails("admin@example.com")
    @Sql(scripts = "classpath:database/booking/add-bookings-to-test-admin-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/booking/remove-bookings-to-test-admin-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteBookingById() throws Exception {
        Long bookingId = 100L;
        mockMvc.perform(delete("/bookings/{id}", bookingId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Successfully update booking status by id")
    @WithUserDetails("admin@example.com")
    @Sql(scripts = "classpath:database/booking/add-bookings-to-test-admin-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/booking/remove-bookings-to-test-admin-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBookingStatus() throws Exception {
        Long bookingId = 100L;
        BookingStatus newStatus = BookingStatus.CANCELED;

        MvcResult result = mockMvc.perform(patch("/bookings/{id}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStatus)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        BookingDto updatedBooking = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookingDto.class);
        assertThat(updatedBooking.getStatus()).isEqualTo(newStatus);
    }

    @Test
    @DisplayName("Successfully create a booking")
    @WithUserDetails("owner@example.com")
    @Sql(scripts = "classpath:database/accommodation/add-accommodation-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts =
            "classpath:database/accommodation/remove-accommodation-with-booking-from-test-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBooking() throws Exception {
        CreateBookingDto createDto = BookingUtils.createBookingDtoToSend();

        MvcResult result = mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        BookingDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookingDto.class);
        assertThat(actual).isNotNull();
        assertThat(actual.getCheckInDate()).isEqualTo(createDto.getCheckInDate());
        assertThat(actual.getCheckOutDate()).isEqualTo(createDto.getCheckOutDate());
        assertThat(actual.getAccommodationId()).isEqualTo(createDto.getAccommodationId());
    }

    @Test
    @DisplayName("Access /bookings/my without authentication returns 401")
    void getAllUserBookings_Unauthenticated() throws Exception {
        mockMvc.perform(get("/bookings/my")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Get booking by non-existent id returns 404")
    @WithUserDetails("admin@example.com")
    @Sql(scripts = "classpath:database/booking/add-bookings-to-test-admin-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/booking/remove-bookings-to-test-admin-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBookingById_NotFound() throws Exception {
        mockMvc.perform(get("/bookings/{id}", 99999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Create booking with invalid data returns 400")
    @WithUserDetails("owner@example.com")
    @Sql(scripts = "classpath:database/booking/add-bookings-to-test-admin-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/booking/remove-bookings-to-test-admin-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBooking_InvalidData() throws Exception {
        CreateBookingDto invalidDto = new CreateBookingDto();
        invalidDto.setCheckInDate(LocalDate.now().plusDays(5));
        invalidDto.setCheckOutDate(LocalDate.now().plusDays(3));
        invalidDto.setAccommodationId(null);
        invalidDto.setUnitId(null);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete booking by non-existent id returns 404")
    @WithUserDetails("admin@example.com")
    @Sql(scripts = "classpath:database/booking/add-bookings-to-test-admin-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/booking/remove-bookings-to-test-admin-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteBookingById_NotFound() throws Exception {
        mockMvc.perform(delete("/bookings/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update booking status by non-existent id returns 404")
    @WithUserDetails("admin@example.com")
    @Sql(scripts = "classpath:database/booking/add-bookings-to-test-admin-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/booking/remove-bookings-to-test-admin-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBookingStatus_NotFound() throws Exception {
        mockMvc.perform(patch("/bookings/{id}", 99999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingStatus.CONFIRMED)))
                .andExpect(status().isNotFound());
    }
}
