package app.controller;

import app.dto.payment.CreatePaymentDto;
import app.dto.payment.PaymentDto;
import app.model.User;
import app.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment controller",
        description = "Endpoints for payment management ( CRUD operations ) + Stripe")
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/my")
    @Operation(summary = "get all user`s payments", description = "Get all user`s payments")
    Page<PaymentDto> getPaymentsForUser(@AuthenticationPrincipal User user, Pageable pageable) {
        return paymentService.getPaymentsForCurrentUser(user, pageable);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "get all user`s payments by id",
            description = "Get all user`s payments by id (admin only)")
    Page<PaymentDto> getPaymentsForUserForAdmin(@PathVariable long userId, Pageable pageable) {
        return paymentService.getPaymentsForAdmin(userId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Initiates payment",
            description = "Initiates payment")
    ResponseEntity<PaymentDto> initiatePayment(@RequestBody CreatePaymentDto createPaymentDto,
                                               @AuthenticationPrincipal User user) {
        try {
            PaymentDto paymentDto = paymentService.createPayment(createPaymentDto, user);
            return ResponseEntity.ok(paymentDto);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
