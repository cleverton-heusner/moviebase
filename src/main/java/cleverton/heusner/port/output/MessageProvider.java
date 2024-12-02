package cleverton.heusner.port.output;

public interface MessageProvider {
    String getMessage(final String keyMessage, final Object... args);
}
