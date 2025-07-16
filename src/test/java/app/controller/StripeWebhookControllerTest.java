package app.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.model.PaymentStatus;
import app.service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class StripeWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Test
    @DisplayName("Should handle checkout.session.completed successfully")
    void handleStripeWebhook_CheckoutSessionCompleted() throws Exception {
        String payload = "{}";
        String sigHeader = "test_signature";
        String sessionId = "cs_test_123";
        String eventId = "evt_test_123";

        Session mockSession = new Session();
        mockSession.setId(sessionId);

        Event mockEvent = mock(Event.class);
        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);

        when(mockEvent.getType()).thenReturn("checkout.session.completed");
        when(mockEvent.getId()).thenReturn(eventId);
        when(mockEvent.getDataObjectDeserializer()).thenReturn(deserializer);
        when(deserializer.getObject()).thenReturn(Optional.of(mockSession));
        when(deserializer.deserializeUnsafe()).thenReturn(mockSession);

        try (MockedStatic<Webhook> webhookMock = mockStatic(Webhook.class)) {
            webhookMock.when(() -> Webhook.constructEvent(eq(payload), eq(sigHeader), anyString()))
                    .thenReturn(mockEvent);

            mockMvc.perform(post("/webhook/stripe")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Stripe-Signature", sigHeader)
                            .content(payload))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Webhook received and processed"));

            verify(paymentService, times(1))
                    .updatePaymentStatus(sessionId, PaymentStatus.PAID);
        }
    }

    @Test
    @DisplayName("Should return 400 Bad Request when Stripe signature is invalid")
    void handleStripeWebhook_InvalidSignature_ReturnsBadRequest() throws Exception {
        String payload = "{}";
        String sigHeader = "invalid_signature";
        try (MockedStatic<Webhook> mockedWebhook = mockStatic(Webhook.class)) {
            mockedWebhook
                    .when(() -> Webhook.constructEvent(eq(payload), eq(sigHeader), any()))
                    .thenThrow(new SignatureVerificationException("Invalid", null));

            mockMvc.perform(post("/webhook/stripe")
                            .header("Stripe-Signature", sigHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Invalid signature"));
        }
    }

    @Test
    @DisplayName("Should return 400 Bad Request when event cannot be parsed")
    void handleStripeWebhook_ParsingError_ReturnsBadRequest() throws Exception {
        String payload = "{}";
        String sigHeader = "test_signature";

        try (MockedStatic<Webhook> mockedWebhook = mockStatic(Webhook.class)) {
            mockedWebhook
                    .when(() -> Webhook.constructEvent(eq(payload), eq(sigHeader), any()))
                    .thenThrow(new RuntimeException("Parsing error"));

            mockMvc.perform(post("/webhook/stripe")
                            .header("Stripe-Signature", sigHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Could not parse event"));
        }
    }

    @Test
    @DisplayName("Should return 500 Internal Server Error when session is null")
    void handleStripeWebhook_SessionNull_ReturnsInternalServerError() throws Exception {
        String payload = "{}";
        String sigHeader = "test_signature";
        String eventId = "evt_test_null_session";

        Event mockEvent = mock(Event.class);
        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);

        when(mockEvent.getType()).thenReturn("checkout.session.completed");
        when(mockEvent.getId()).thenReturn(eventId);
        when(mockEvent.getDataObjectDeserializer()).thenReturn(deserializer);
        when(deserializer.deserializeUnsafe()).thenReturn(null);

        try (MockedStatic<Webhook> mockedWebhook = mockStatic(Webhook.class)) {
            mockedWebhook
                    .when(() -> Webhook.constructEvent(eq(payload), eq(sigHeader), any()))
                    .thenReturn(mockEvent);

            mockMvc.perform(post("/webhook/stripe")
                            .header("Stripe-Signature", sigHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("Session object is null"));
        }
    }
}
