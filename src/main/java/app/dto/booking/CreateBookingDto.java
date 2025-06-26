package app.dto.booking;

import app.validation.user.CheckInAndOutDate;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@CheckInAndOutDate
public class CreateBookingDto {
    @NotNull
    @FutureOrPresent
    private LocalDate checkInDate;
    @NotNull
    @Future
    private LocalDate checkOutDate;
    @NotNull
    private Long accommodationId;
    @NotNull
    private Long unitId;
}
