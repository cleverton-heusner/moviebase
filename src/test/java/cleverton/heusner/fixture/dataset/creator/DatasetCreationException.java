package cleverton.heusner.fixture.dataset.creator;

public class DatasetCreationException extends RuntimeException {

    public DatasetCreationException(final String message) {
        super(message);
    }

    public DatasetCreationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DatasetCreationException(final Throwable cause) {
        super(cause);
    }
}
