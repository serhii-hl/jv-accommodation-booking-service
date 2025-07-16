package app.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import app.dto.stripe.StripeSessionResponse;
import app.service.impl.StripeServiceImpl;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StripeServiceTests {
    @InjectMocks
    private StripeServiceImpl stripeService;

    @Test
    @DisplayName("Should return existing unused and unexpired token for user")
    void buildCorrectSessionRequest() throws StripeException {
        BigDecimal amount = new BigDecimal("450.00");
        String currency = "USD";
        String bookingId = "100";
        String successUrl = "http://localhost/success";
        String cancelUrl = "http://localhost/cancel";
        try (MockedStatic<Session> sessionMockedStatic = mockStatic(Session.class)) {
            Session session = mock(Session.class);
            when(session.getUrl()).thenReturn("http://mocked.url");
            when(session.getId()).thenReturn("mocked_session_id");
            sessionMockedStatic.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenReturn(session);
            StripeSessionResponse response = stripeService.createCheckoutSession(
                    amount, currency, bookingId, successUrl, cancelUrl
            );
            assertThat(response.getSessionUrl()).isEqualTo("http://mocked.url");
            assertThat(response.getSessionId()).isEqualTo("mocked_session_id");
        }
    }
}
