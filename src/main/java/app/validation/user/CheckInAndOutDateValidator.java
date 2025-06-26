package app.validation.user;

import app.dto.booking.CreateBookingDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CheckInAndOutDateValidator
        implements ConstraintValidator<CheckInAndOutDate, CreateBookingDto> {
    @Override
    public boolean isValid(CreateBookingDto dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getCheckInDate() == null || dto.getCheckOutDate() == null) {
            return false;
        }
        return dto.getCheckOutDate().isAfter(dto.getCheckInDate());
    }
}
