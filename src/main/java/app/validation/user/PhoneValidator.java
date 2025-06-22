package app.validation.user;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<Phone, String> {
    private static final String PHONE_PATTERN = "^\\+380\\d{9}$";

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext constraintValidatorContext) {
        return phone != null && Pattern.compile(PHONE_PATTERN).matcher(phone).matches();
    }
}
