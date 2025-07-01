package app.service.impl;

import app.dto.payment.CreatePaymentDto;
import app.dto.payment.PaymentDto;
import app.dto.stripe.StripeSessionResponse;
import app.mapper.PaymentMapper;
import app.model.Booking;
import app.model.BookingStatus;
import app.model.Payment;
import app.model.PaymentStatus;
import app.model.Role;
import app.model.User;
import app.repository.BookingRepository;
import app.repository.PaymentRepository;
import app.service.PaymentService;
import app.service.StripeService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final String CURRENCYCODE = "USD";

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final BookingRepository bookingRepository;
    private final StripeService stripeService;

    @Value("${stripe.success.url}")
    private String stripeSuccessUrl;

    @Value("${stripe.cancel.url}")
    private String stripeCancelUrl;

    @Override
    public Page<PaymentDto> getPaymentsForCurrentUser(User user, Pageable pageable) {
        return paymentRepository.findAllByUserId(user.getId(), pageable).map(paymentMapper::toDto);
    }

    @Override
    public Page<PaymentDto> getPaymentsForAdmin(Long userId, Pageable pageable) {
        return paymentRepository.findAllByUserId(userId, pageable).map(paymentMapper::toDto);
    }

    @Override
    @Transactional
    public PaymentDto createPayment(CreatePaymentDto createPaymentDto, User user) {
        Payment payment = new Payment();
        Booking booking = bookingRepository
                .findById(createPaymentDto.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Booking not found for id: "
                        + createPaymentDto.getBookingId()));
        if (!user.getRole().equals(Role.ADMIN)
                && !Objects.equals(booking.getUser().getId(), user.getId())) {
            throw new SecurityException(
                    "You are not authorized to create payment for this booking.");
        }
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new SecurityException(
                    "You can`t make a payment for status " + booking.getStatus());
        }
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setPrice(booking.getTotalPrice());
        payment.setBooking(booking);
        payment.setCurrency(CURRENCYCODE);
        payment.setBookingDate(LocalDateTime.now());
        try {
            StripeSessionResponse sessionResponse = stripeService.createCheckoutSession(
                    payment.getPrice(),
                    payment.getCurrency(),
                    booking.getId().toString(),
                    stripeSuccessUrl,
                    stripeCancelUrl
            );
            payment.setSessionUrl(sessionResponse.getSessionUrl());
            payment.setSessionId(sessionResponse.getSessionId());
        } catch (Exception e) {
            throw new RuntimeException("Cannot create stripe session response: " + e.getMessage());
        }
        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toDto(savedPayment);
    }

    @Override
    public void updatePaymentStatus(String sessionId, PaymentStatus status) {
        Payment payment = paymentRepository
                .findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Payment not found for session ID: " + sessionId));
        payment.setPaymentStatus(status);
        paymentRepository.save(payment);
    }
}
