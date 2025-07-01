package app.controller.web;

import app.service.StripeService;
import com.stripe.model.checkout.Session;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Tag(name = "Stripe Page Controller",
        description = "Endpoints for Stripe payment redirects and HTML page rendering")
@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class StripePageController {

    private final StripeService stripeService;

    @Value("${app.public.base-url}")
    private String appPublicBaseUrl;

    @Operation(summary = "Endpoint for successful payment redirect from Stripe",
            description = "Handles Stripe's redirect after a successful payment "
                    + "and redirects to a user-friendly page.")
    @GetMapping("/success")
    public RedirectView handleStripeSuccess(@RequestParam("session_id") String sessionId) {
        try {
            Session session = stripeService.retrieveSession(sessionId);
            String bookingId = session.getMetadata().get("booking_id");
            return new RedirectView(appPublicBaseUrl
                    + "/payments/payment-success?bookingId=" + bookingId);
        } catch (Exception e) {
            return new RedirectView(appPublicBaseUrl + "/payments/payment-error");
        }
    }

    @Operation(summary = "Endpoint for canceled payment redirect from Stripe",
            description = "Handles Stripe's redirect after a canceled payment "
                    + "and redirects to a user-friendly page.")
    @GetMapping("/cancel")
    public RedirectView handleStripeCancel() {
        return new RedirectView(appPublicBaseUrl + "/payments/payment-cancelled");
    }

    @Operation(summary = "Payment successful page",
            description = "Displays a message for successful payment")
    @GetMapping("/payment-success")
    public String paymentSuccessPage(@RequestParam("bookingId") String bookingId, Model model) {
        model.addAttribute("bookingId", bookingId);
        return "paymentSuccess";
    }

    @Operation(summary = "Payment error page",
            description = "Displays a message for payment error")
    @GetMapping("/payment-error")
    public String paymentErrorPage() {
        return "paymentError";
    }

    @Operation(summary = "Payment canceled page",
            description = "Displays a message for canceled payment")
    @GetMapping("/payment-cancelled")
    public String paymentCancelledPage() {
        return "paymentCancelled";
    }
}
