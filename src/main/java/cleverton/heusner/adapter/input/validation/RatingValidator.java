package cleverton.heusner.adapter.input.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class RatingValidator implements ConstraintValidator<Rating, Double> {

    private static final String RATING_REGEX = "^(10(\\.0|\\.|)|[0-9](\\.[0-9])?)$";

    @Override
    public boolean isValid(final Double rating, final ConstraintValidatorContext context) {
        return Pattern.matches(RATING_REGEX, String.valueOf(rating));
    }
}