package app.util;

import app.model.Payment;
import app.model.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentUtils {
    public static Payment createExpectedPayment() {
        Payment payment = new Payment();
        payment.setId(100L);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setBooking(BookingUtils.createExpectedBooking());
        payment.setSessionUrl("http://stripe.com/session_100");
        payment.setSessionId("cs_test_123456789");
        payment.setPrice(BigDecimal.valueOf(450.00));
        payment.setCurrency("USD");
        payment.setBookingDate(LocalDateTime.of(2025,7, 5, 0, 0));
        payment.setDeleted(false);
        return payment;
    }
}
