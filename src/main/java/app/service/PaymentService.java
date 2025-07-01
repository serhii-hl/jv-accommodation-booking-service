package app.service;

import app.dto.payment.CreatePaymentDto;
import app.dto.payment.PaymentDto;
import app.model.PaymentStatus;
import app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    Page<PaymentDto> getPaymentsForCurrentUser(User user, Pageable pageable);

    Page<PaymentDto> getPaymentsForAdmin(Long userId, Pageable pageable);

    PaymentDto createPayment(CreatePaymentDto createPaymentDto, User user);

    void updatePaymentStatus(String sessionId, PaymentStatus status);
}
