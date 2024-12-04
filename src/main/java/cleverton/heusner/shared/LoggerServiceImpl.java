package cleverton.heusner.shared;

import cleverton.heusner.port.shared.LoggerService;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class LoggerServiceImpl implements LoggerService {

    private static final Logger logger = LoggerFactory.getLogger(LoggerServiceImpl.class);

    @Override
    public void info(final String message, final Object... params) {
        logger.info(formatMessage(message), params);
    }

    @Override
    public void warn(final String message, final Object... params) {
        logger.warn(formatMessage(message), params);
    }

    @Override
    public void error(final String message, final Object... params) {
        logger.error(formatMessage(message), params);
    }

    private String formatMessage(final String message) {
        return message.replace("%", "{}");
    }
}