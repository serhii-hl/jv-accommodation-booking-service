package app.validation.user;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class NewPhoneValidator implements ConstraintValidator<NewPhone, String> {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+\\d{9}$");

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null) {
            return true;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }
}
