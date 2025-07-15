package app.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentDto {
    @NotNull
    private Long bookingId;
}
