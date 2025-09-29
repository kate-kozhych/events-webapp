package daw.app.model.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.logging.Logger;

public class EsnCardValidator implements ConstraintValidator<EsnCard, String> {

    private final Logger logger = Logger.getLogger(EsnCardValidator.class.getName());

    @Override
    public boolean isValid(String esnCard, ConstraintValidatorContext context) {
        if (esnCard == null) {
            return false;
        }

        logger.info("Validating ESN Card: " + esnCard);

        boolean startsWithESN = esnCard.startsWith("ESN");
        boolean hasMinLength = esnCard.length() >= 6;

        if (!startsWithESN || !hasMinLength) {
            context.disableDefaultConstraintViolation();

            if (!startsWithESN) {
                context.buildConstraintViolationWithTemplate("ESN Card must start with 'ESN'")
                        .addConstraintViolation();
            } else {
                context.buildConstraintViolationWithTemplate("ESN Card must be at least 6 characters long")
                        .addConstraintViolation();
            }

            return false;
        }

        return true;
    }
}
