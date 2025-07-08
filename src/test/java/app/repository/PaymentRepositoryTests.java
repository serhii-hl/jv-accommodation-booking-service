package app.repository;

import app.model.Payment;
import app.util.PaymentUtils;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/booking/add-booking-to-test-db.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/payment/add-payment-to-test-db.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/payment/remove-payment-from-test-db.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PaymentRepositoryTests {
    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("Find all payments by user id")
    void findAllPaymentsByUserId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Payment> result = paymentRepository.findAllByUserId(100L, pageable);
        Payment payment = PaymentUtils.createExpectedPayment();
        Page<Payment> expected = new PageImpl<>(
                Collections.singletonList(payment),
                pageable, 1);
        Assertions.assertEquals(expected, result);
    }

    @Test
    @DisplayName("Find all payments by user id")
    void findBySessionId() {
        Optional<Payment> result = paymentRepository.findBySessionId("cs_test_123456789");
        Optional<Payment> expected = Optional.of(PaymentUtils.createExpectedPayment());
        Assertions.assertEquals(expected, result);
    }
}
