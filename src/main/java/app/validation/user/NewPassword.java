package app.validation.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NewPasswordValidator.class)
@Target({ElementType.PARAMETER,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NewPassword {
    String message() default
            "Invalid password. Please use at least 8 characters and 1 special character "
                    + "OR don`t put any value if you don`t want to change it";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
