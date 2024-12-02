package cleverton.heusner.adapter.output.provider;

import cleverton.heusner.port.output.MessageProvider;
import jakarta.enterprise.context.Dependent;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

@Dependent
public class MessageProviderImpl implements MessageProvider {

    private final ResourceBundle bundle;

    public MessageProviderImpl(final Locale locale) {
        bundle = ResourceBundle.getBundle("messages", locale);
    }

    @Override
    public String getMessage(final String keyMessage, final Object... args) {
        return MessageFormat.format(bundle.getString(keyMessage), args);
    }
}