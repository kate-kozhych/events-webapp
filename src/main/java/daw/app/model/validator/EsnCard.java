package daw.app.model.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = EsnCardValidator.class)
@Retention(RUNTIME)
@Target(ElementType.FIELD)
public @interface EsnCard {

    String message() default "ESN Card must start with 'ESN' and be at least 6 characters long";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
