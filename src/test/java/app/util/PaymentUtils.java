package app.util;

import app.dto.payment.CreatePaymentDto;
import app.dto.payment.PaymentDto;
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

    public static PaymentDto createExpectedPaymentDto() {
        Payment payment = createExpectedPayment();
        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());
        dto.setPaymentStatus(payment.getPaymentStatus());
        if (payment.getBooking() != null) {
            dto.setBookingId(payment.getBooking().getId());
        }
        dto.setPrice(payment.getPrice());
        dto.setCurrency(payment.getCurrency());
        dto.setSessionUrl(payment.getSessionUrl());
        dto.setSessionId(payment.getSessionId());
        return dto;
    }

    public static CreatePaymentDto createCreatePaymentDtoToSend() {
        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setBookingId(100L);
        return dto;
    }
}
