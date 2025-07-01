package app.dto.payment;

import app.model.PaymentStatus;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentDto {
    private Long id;
    private PaymentStatus paymentStatus;
    private Long bookingId;
    private BigDecimal price;
    private String currency;
    private String sessionUrl;
    private String sessionId;
}
