package app.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.dto.payment.CreatePaymentDto;
import app.dto.payment.PaymentDto;
import app.dto.stripe.StripeSessionResponse;
import app.service.StripeService;
import app.util.PaymentUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.StripeException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
public class PaymentControllerTests {
    private static final Pageable PAGEABLE = PageRequest.of(0, 10);

    @MockBean
    private StripeService stripeService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(@Autowired WebApplicationContext context) throws StripeException {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        when(stripeService.createCheckoutSession(
                any(), any(), any(), any(), any()
        )).thenReturn(new StripeSessionResponse("http://stripe.com/session_100", "cs_test_123456789"));
    }

    @Test
    @DisplayName("Successfully return all user`s payments")
    @WithUserDetails("owner@example.com")
    @Sql(scripts = "classpath:database/booking/add-booking-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/payment/add-payment-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/payment/remove-payment-from-test-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "classpath:database/booking/remove-booking-from-test-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAllUserPayments() throws Exception {
        PaymentDto expected = PaymentUtils.createExpectedPaymentDto();
        List<PaymentDto> expectedContent = List.of(expected);
        MvcResult result = mockMvc.perform(get("/payments/my")
                        .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                        .param("size", String.valueOf(PAGEABLE.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        List<PaymentDto> actualContent = objectMapper.convertValue(
                rootNode.get("content"), new TypeReference<List<PaymentDto>>() {}
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
    @Sql(scripts = "classpath:database/payment/add-payment-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/payment/remove-payment-from-test-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "classpath:database/booking/remove-bookings-to-test-admin-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBookingByIdAdmin() throws Exception {
        Long userId = 100L;
        PaymentDto expected = PaymentUtils.createExpectedPaymentDto();
        List<PaymentDto> expectedContent = List.of(expected);
        MvcResult result = mockMvc.perform(get("/payments/{userId}", userId)
                        .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                        .param("size", String.valueOf(PAGEABLE.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        List<PaymentDto> actualContent = objectMapper.convertValue(
                rootNode.get("content"), new TypeReference<List<PaymentDto>>() {}
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
    @DisplayName("Successfully create payment")
    @WithUserDetails("owner@example.com")
    @Sql(scripts = {
            "classpath:database/booking/add-pending-booking-to-test-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/payment/remove-payment-and-booking-from-test-db.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createPayment_Success() throws Exception {
        CreatePaymentDto dto = PaymentUtils.createCreatePaymentDtoToSend();
        PaymentDto expected = PaymentUtils.createExpectedPaymentDto();

        MvcResult result = mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        PaymentDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), PaymentDto.class);

        assertThat(actual).isNotNull();
        assertThat(actual.getBookingId()).isEqualTo(expected.getBookingId());
        assertThat(actual.getPaymentStatus()).isEqualTo(expected.getPaymentStatus());
        assertThat(actual.getCurrency()).isEqualTo(expected.getCurrency());
        assertThat(actual.getPrice()).isEqualByComparingTo(expected.getPrice());
        assertThat(actual.getSessionId()).isEqualTo(expected.getSessionId());
        assertThat(actual.getSessionUrl()).isEqualTo(expected.getSessionUrl());
    }

    @Test
    @DisplayName("Successfully return empty payment list for new user")
    @WithUserDetails("owner@example.com")
    @Sql(scripts = "classpath:database/user/add-user-to-test-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/user/remove-user-from-test-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getEmptyPaymentsForUser() throws Exception {
        MvcResult result = mockMvc.perform(get("/payments/my")
                        .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                        .param("size", String.valueOf(PAGEABLE.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        List<PaymentDto> actualContent = objectMapper.convertValue(
                rootNode.get("content"), new TypeReference<List<PaymentDto>>() {}
        );
        long actualTotalElements = rootNode.get("totalElements").asLong();

        assertThat(actualContent).isEmpty();
        assertThat(actualTotalElements).isEqualTo(0);
    }

    @Test
    @DisplayName("Should return 403 Forbidden when user is not authorized to create payment")
    @WithUserDetails("customer@example.com")
    @Sql(scripts = {
            "classpath:database/user/add-customer-user-to-test-db.sql",
            "classpath:database/booking/add-pending-booking-to-test-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/payment/remove-payment-and-booking-from-test-db.sql",
            "classpath:database/user/remove-customer-user-from-test-db.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createPayment_UnauthorizedUser_ReturnsForbidden() throws Exception {
        CreatePaymentDto dto = PaymentUtils.createCreatePaymentDtoToSend();
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 403 Forbidden when booking status is not PENDING")
    @WithUserDetails("owner@example.com")
    @Sql(scripts = {
            "classpath:database/booking/add-booking-to-test-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/payment/remove-payment-and-booking-from-test-db.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createPayment_InvalidBookingStatus_ReturnsForbidden() throws Exception {
        CreatePaymentDto dto = PaymentUtils.createCreatePaymentDtoToSend();
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 404 Not Found when booking does not exist")
    @WithUserDetails("owner@example.com")
    @Sql(scripts = {
            "classpath:database/booking/add-pending-booking-to-test-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/payment/remove-payment-and-booking-from-test-db.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createPayment_BookingNotFound_ReturnsNotFound() throws Exception {
        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setBookingId(1L);
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when generic RuntimeException occurs")
    @WithUserDetails("owner@example.com")
    @Sql(scripts = {
            "classpath:database/booking/add-pending-booking-to-test-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/payment/remove-payment-and-booking-from-test-db.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createPayment_GenericRuntimeException_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString("abcd")))
                .andExpect(status().isBadRequest());
    }
}
