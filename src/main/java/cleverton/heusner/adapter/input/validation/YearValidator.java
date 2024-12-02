package cleverton.heusner.adapter.input.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class YearValidator implements ConstraintValidator<Year, Integer> {

    private static final String YEAR_REGEX = "^\\d{4}$";

    @Override
    public boolean isValid(final Integer year, final ConstraintValidatorContext context) {
        return Pattern.matches(YEAR_REGEX, String.valueOf(year));
    }
}