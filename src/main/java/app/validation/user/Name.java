package app.validation.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NameValidator.class)
@Target({ElementType.PARAMETER,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Name {
    String message() default
            "Invalid name. Please use at least two letters and don`t use numbers/symbols";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
