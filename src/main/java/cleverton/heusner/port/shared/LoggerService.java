package cleverton.heusner.port.shared;

public interface LoggerService {

    void info(final String message, final Object... params);
    void error(final String message, final Object... params);
    void warn(final String message, final Object... params);
}
