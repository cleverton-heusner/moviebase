package cleverton.heusner.adapter.input.configuration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;

import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class LocaleConfiguration {

    @Context
    private HttpHeaders headers;

    @Produces
    @Dependent
    public Locale locale() {
        final List<Locale> locales = headers.getAcceptableLanguages();
        return locales.isEmpty() ? Locale.getDefault() : locales.getFirst();
    }
}