package app.validation.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NewPhoneValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NewPhone {
    String message() default
            "Invalid phone number. Please use the correct one";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
