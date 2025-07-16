package app.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import app.config.SecurityConfig;
import app.service.StripeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.checkout.Session;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class StripePageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private StripeService stripeService;

    @Value("${app.public.base-url}")
    private String appPublicBaseUrl = "http://localhost:3000";

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void handleStripeSuccess_validSessionId_redirectsToSuccessPage() throws Exception {
        Session session = new Session();
        Map<String, String> metadata = new HashMap<>();
        metadata.put("booking_id", "100");
        session.setMetadata(metadata);

        when(stripeService.retrieveSession("cs_test_123")).thenReturn(session);

        mockMvc.perform(get("/payments/success")
                        .param("session_id", "cs_test_123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(appPublicBaseUrl
                        + "/payments/payment-success?bookingId=100"));
    }

    @Test
    void handleStripeSuccess_invalidSessionId_redirectsToErrorPage() throws Exception {
        when(stripeService.retrieveSession("bad")).thenThrow(new RuntimeException("Stripe error"));

        mockMvc.perform(get("/payments/success")
                        .param("session_id", "bad"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(appPublicBaseUrl + "/payments/payment-error"));
    }

    @Test
    void handleStripeCancel_redirectsToCancelPage() throws Exception {
        mockMvc.perform(get("/payments/cancel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(appPublicBaseUrl + "/payments/payment-cancelled"));
    }

    @Test
    void paymentSuccessPage_displaysBookingId() throws Exception {
        mockMvc.perform(get("/payments/payment-success")
                        .param("bookingId", "101"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("bookingId", "101"))
                .andExpect(view().name("paymentSuccess"));
    }

    @Test
    void paymentErrorPage_returnsView() throws Exception {
        mockMvc.perform(get("/payments/payment-error"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentError"));
    }

    @Test
    void paymentCancelledPage_returnsView() throws Exception {
        mockMvc.perform(get("/payments/payment-cancelled"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentCancelled"));
    }
}
