package app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

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
import app.service.impl.PaymentServiceImpl;
import app.util.BookingUtils;
import app.util.PaymentUtils;
import app.util.UserUtils;
import com.stripe.exception.ApiException;
import com.stripe.exception.StripeException;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTests {
    private static final Pageable PAGEABLE = PageRequest.of(0, 10);

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private StripeService stripeService;

    @Test
    @DisplayName("get payment for user - success")
    void getPaymentsForUserSuccess() {
        Payment payment = PaymentUtils.createExpectedPayment();
        PaymentDto paymentDto = PaymentUtils.createExpectedPaymentDto();
        Page<Payment> pagePayment = new PageImpl<>(List.of(payment), PAGEABLE, 1);
        when(paymentRepository.findAllByUserId(100L, PAGEABLE)).thenReturn(pagePayment);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);
        User user = UserUtils.createExpectedTestUserOwner();
        Page<PaymentDto> actual = paymentService.getPaymentsForCurrentUser(user, PAGEABLE);
        Page<PaymentDto> expected = new PageImpl<>(List.of(paymentDto), PAGEABLE, 1);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("get payment for admin - success")
    void getPaymentsForAdminSuccess() {
        Payment payment = PaymentUtils.createExpectedPayment();
        PaymentDto paymentDto = PaymentUtils.createExpectedPaymentDto();
        Page<Payment> pagePayment = new PageImpl<>(List.of(payment), PAGEABLE, 1);
        when(paymentRepository.findAllByUserId(100L, PAGEABLE)).thenReturn(pagePayment);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);
        Page<PaymentDto> actual = paymentService.getPaymentsForAdmin(100L, PAGEABLE);
        Page<PaymentDto> expected = new PageImpl<>(List.of(paymentDto), PAGEABLE, 1);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Should successfully create a payment for a pending booking by owner")
    void createPaymentSuccess_Owner() throws StripeException {
        Long bookingId = 100L;
        CreatePaymentDto createPaymentDto = new CreatePaymentDto();
        createPaymentDto.setBookingId(bookingId);
        Booking booking = BookingUtils.createExpectedBooking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.PENDING);
        User user = UserUtils.createExpectedTestUserOwner();
        booking.setUser(user);
        StripeSessionResponse stripeSessionResponse = new StripeSessionResponse(
                "http://stripe.com/new_session_url", "new_session_id");

        Payment expectedSavedPayment = PaymentUtils.createExpectedPayment();
        expectedSavedPayment.setBooking(booking);
        expectedSavedPayment.setSessionUrl(stripeSessionResponse.getSessionUrl());
        expectedSavedPayment.setSessionId(stripeSessionResponse.getSessionId());
        expectedSavedPayment.setPaymentStatus(PaymentStatus.PENDING);

        PaymentDto expectedDto = PaymentUtils.createExpectedPaymentDto();
        expectedDto.setSessionUrl(stripeSessionResponse.getSessionUrl());
        expectedDto.setSessionId(stripeSessionResponse.getSessionId());
        expectedDto.setPaymentStatus(PaymentStatus.PENDING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        when(stripeService.createCheckoutSession(
                eq(new BigDecimal("450.00")), eq("USD"),
                eq(String.valueOf(bookingId)), isNull(), isNull()))
                .thenReturn(stripeSessionResponse);
        when(paymentRepository.save(any(Payment.class))).thenReturn(expectedSavedPayment);
        when(paymentMapper.toDto(any(Payment.class))).thenReturn(expectedDto);
        PaymentDto actualDto = paymentService.createPayment(createPaymentDto, user);
        assertThat(actualDto)
                .usingRecursiveComparison()
                .isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("Should successfully create a payment for a pending booking by admin")
    void createPaymentSuccess_Admin() throws StripeException {
        Long bookingId = 100L;
        User adminUser = UserUtils.createCustomerUser(200L, "admin@example.com",
                "Admin", "User",
                "+1234567891", "1234567890");
        adminUser.setRole(Role.ADMIN);
        CreatePaymentDto createPaymentDto = new CreatePaymentDto();
        createPaymentDto.setBookingId(bookingId);
        Booking booking = BookingUtils.createExpectedBooking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.PENDING);
        User bookingUser = UserUtils.createCustomerUser(101L,
                "customer@example.com", "Cust",
                "User", "+1234567892", "1234567893");
        booking.setUser(bookingUser);
        StripeSessionResponse stripeSessionResponse = new StripeSessionResponse(
                "http://stripe.com/admin_session_url", "admin_session_id");
        Payment expectedSavedPayment = PaymentUtils.createExpectedPayment();
        expectedSavedPayment.setBooking(booking);
        expectedSavedPayment.setSessionUrl(stripeSessionResponse.getSessionUrl());
        expectedSavedPayment.setSessionId(stripeSessionResponse.getSessionId());
        expectedSavedPayment.setPaymentStatus(PaymentStatus.PENDING);
        PaymentDto expectedDto = PaymentUtils.createExpectedPaymentDto();
        expectedDto.setSessionUrl(stripeSessionResponse.getSessionUrl());
        expectedDto.setSessionId(stripeSessionResponse.getSessionId());
        expectedDto.setPaymentStatus(PaymentStatus.PENDING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(stripeService.createCheckoutSession(
                eq(new BigDecimal("450.00")), eq("USD"),
                eq("100"), isNull(), isNull()))
                .thenReturn(stripeSessionResponse);
        when(paymentRepository.save(any(Payment.class))).thenReturn(expectedSavedPayment);
        when(paymentMapper.toDto(any(Payment.class))).thenReturn(expectedDto);
        PaymentDto actualDto = paymentService.createPayment(createPaymentDto, adminUser);
        assertThat(actualDto)
                .usingRecursiveComparison()
                .isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("Should throw SecurityException when user is not authorized to create payment")
    void createPaymentFailure_UnauthorizedUser() {
        Long bookingId = 100L;
        User unauthorizedUser = UserUtils.createCustomerUser(999L,
                "unauth@example.com", "Unauth", "User",
                "+1234567899", "9876543210");
        unauthorizedUser.setRole(Role.USER);

        CreatePaymentDto createPaymentDto = new CreatePaymentDto();
        createPaymentDto.setBookingId(bookingId);

        Booking booking = BookingUtils.createExpectedBooking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.PENDING);
        booking.setUser(UserUtils.createExpectedTestUserOwner());
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        SecurityException thrown = assertThrows(
                SecurityException.class,
                () -> paymentService.createPayment(createPaymentDto, unauthorizedUser),
                "Expected SecurityException for unauthorized user"
        );
        assertThat(thrown.getMessage()).isEqualTo(
                "You are not authorized to create payment for this booking.");
    }

    @Test
    @DisplayName("Should throw SecurityException when booking status is not PENDING")
    void createPaymentFailure_InvalidBookingStatus() {
        Long bookingId = 100L;
        CreatePaymentDto createPaymentDto = new CreatePaymentDto();
        createPaymentDto.setBookingId(bookingId);
        Booking booking = BookingUtils.createExpectedBooking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.CONFIRMED);
        User user = UserUtils.createExpectedTestUserOwner();
        booking.setUser(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        SecurityException thrown = assertThrows(
                SecurityException.class,
                () -> paymentService.createPayment(createPaymentDto, user),
                "Expected SecurityException for invalid booking status"
        );
        assertThat(thrown.getMessage()).isEqualTo(
                "You can`t make a payment for status " + booking.getStatus());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when booking is not found")
    void createPaymentFailure_BookingNotFound() {
        Long nonExistentBookingId = 999L;
        CreatePaymentDto createPaymentDto = new CreatePaymentDto();
        createPaymentDto.setBookingId(nonExistentBookingId);
        when(bookingRepository.findById(nonExistentBookingId)).thenReturn(Optional.empty());
        User user = UserUtils.createExpectedTestUserOwner();
        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> paymentService.createPayment(createPaymentDto, user),
                "Expected EntityNotFoundException when booking not found"
        );
        assertThat(thrown.getMessage()).isEqualTo("Booking not found for id: "
                + nonExistentBookingId);
    }

    @Test
    @DisplayName("Should throw RuntimeException when Stripe session creation fails")
    void createPaymentFailure_StripeServiceException() throws StripeException {
        Long bookingId = 100L;
        CreatePaymentDto createPaymentDto = new CreatePaymentDto();
        createPaymentDto.setBookingId(bookingId);
        Booking booking = BookingUtils.createExpectedBooking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.PENDING);
        User user = UserUtils.createExpectedTestUserOwner();
        booking.setUser(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        doThrow(new ApiException("Stripe API error", null, null, null, null))
                .when(stripeService).createCheckoutSession(
                        eq(new BigDecimal("450.00")), eq("USD"),
                        eq("100"), isNull(), isNull());
        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> paymentService.createPayment(createPaymentDto, user),
                "Expected RuntimeException when Stripe session creation fails"
        );
        assertThat(thrown.getMessage())
                .contains("Cannot create stripe session response: Stripe API error");
    }
}
