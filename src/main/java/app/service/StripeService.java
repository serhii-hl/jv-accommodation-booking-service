package app.service;

import app.dto.stripe.StripeSessionResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;

public interface StripeService {

    StripeSessionResponse createCheckoutSession(
            BigDecimal amount, String currency, String bookingId,
            String successUrl, String cancelUrl) throws StripeException;

    Session retrieveSession(String sessionId) throws StripeException;
}
