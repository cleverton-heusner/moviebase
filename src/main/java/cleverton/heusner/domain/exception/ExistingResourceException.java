package cleverton.heusner.domain.exception;

public class ExistingResourceException extends RuntimeException {

    public ExistingResourceException(final String message) {
        super(message);
    }
}