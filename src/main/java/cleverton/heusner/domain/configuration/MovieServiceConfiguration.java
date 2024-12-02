package cleverton.heusner.domain.configuration;

import cleverton.heusner.port.output.MessageProvider;
import cleverton.heusner.domain.service.MovieServiceImpl;
import cleverton.heusner.port.input.service.MovieProvider;
import cleverton.heusner.port.input.service.MovieService;
import cleverton.heusner.port.output.AppInfoProvider;
import cleverton.heusner.port.output.MovieRatingProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class MovieServiceConfiguration {

    @Produces
    @RequestScoped
    public MovieService movieService(final MovieProvider movieProvider,
                                     final MovieRatingProvider movieRatingProvider,
                                     final AppInfoProvider appInfoProvider,
                                     final MessageProvider messageProvider) {
        return new MovieServiceImpl(movieProvider, movieRatingProvider, appInfoProvider, messageProvider);
    }
}