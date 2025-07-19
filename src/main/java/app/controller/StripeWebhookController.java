package app.controller;

import app.model.PaymentStatus;
import app.service.PaymentService;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Stripe webhook controller", description = "Endpoints for Stripe webhooks")
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    private final PaymentService paymentService;

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader)
            throws EventDataObjectDeserializationException {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not parse event");
        }

        switch (event.getType()) {
            case "checkout.session.completed":
                Session sessionCompleted = (Session) event
                        .getDataObjectDeserializer().deserializeUnsafe();
                if (sessionCompleted != null) {
                    try {
                        String sessionId = sessionCompleted.getId();
                        paymentService.updatePaymentStatus(sessionId, PaymentStatus.PAID);
                    } catch (EntityNotFoundException e) {
                        return ResponseEntity.ok("Processed with warning: " + e.getMessage());
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error processing event");
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Session object is null");
                }
                break;

            default:
                System.err.println("Unhandled event type: " + event.getType());
                break;
        }
        return ResponseEntity.ok("Webhook received and processed");
    }
}
