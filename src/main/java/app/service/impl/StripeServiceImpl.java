package app.service.impl;

import app.dto.stripe.StripeSessionResponse;
import app.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeServiceImpl implements StripeService {

    public StripeServiceImpl(@Value("${stripe.api.key}") String secretKey) {
        Stripe.apiKey = secretKey;
    }

    @Override
    public StripeSessionResponse createCheckoutSession(
            BigDecimal amount, String currency, String bookingId,
            String successUrl, String cancelUrl) throws StripeException {
        long amountInCents = amount.multiply(new BigDecimal("100")).longValue();
        SessionCreateParams.LineItem bookingLineItem =
                createBookingLineItem(amountInCents, currency, bookingId, amount);
        SessionCreateParams params = SessionCreateParams
                .builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl)
                .addLineItem(bookingLineItem)
                .putMetadata("booking_id", bookingId)
                .build();
        Session session = Session.create(params);
        return new StripeSessionResponse(session.getUrl(), session.getId());
    }

    @Override
    public Session retrieveSession(String sessionId) throws StripeException {
        return Session.retrieve(sessionId);
    }

    private SessionCreateParams.LineItem createBookingLineItem(
            long amountInCents, String currency,
            String bookingId, BigDecimal originalAmount) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(currency)
                .setUnitAmount(amountInCents)
                .setProductData(
                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName("Payment for booking " + bookingId)
                                .setDescription("Booking #" + bookingId + " for "
                                        + originalAmount + " " + currency.toUpperCase())
                                .build())
                        .build())
                .build();
    }
}
