package cleverton.heusner.domain.service;

import cleverton.heusner.domain.exception.BusinessException;
import cleverton.heusner.domain.exception.ExistingResourceException;
import cleverton.heusner.domain.exception.ResourceNotFoundException;
import cleverton.heusner.domain.model.Movie;
import cleverton.heusner.port.input.service.MovieProvider;
import cleverton.heusner.port.input.service.MovieService;
import cleverton.heusner.port.output.AppInfoProvider;
import cleverton.heusner.port.output.MessageProvider;
import cleverton.heusner.port.output.MovieRatingProvider;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class MovieServiceImpl implements MovieService {

    private final MovieProvider movieProvider;
    private final MovieRatingProvider movieRatingProvider;
    private final AppInfoProvider appInfoProvider;
    private final MessageProvider messageProvider;

    public MovieServiceImpl(final MovieProvider movieProvider,
                            final MovieRatingProvider movieRatingProvider,
                            final AppInfoProvider appInfoProvider,
                            final MessageProvider messageProvider) {
        this.movieProvider = movieProvider;
        this.movieRatingProvider = movieRatingProvider;
        this.appInfoProvider = appInfoProvider;
        this.messageProvider = messageProvider;
    }

    @Override
    public Movie create(final Movie movie) {
        validateIfExistingMovie(movie.getIsan());
        validateReleaseYear(movie);

        movie.setCreationDateTime(LocalDateTime.now());
        return movieProvider.create(movie);
    }

    @Override
    public Long countMovies() {
        return movieProvider.countMovies();
    }

    @Override
    public List<Movie> list() {
        return movieProvider.list();
    }

    @Override
    public Movie getRatingsById(final Long movieId) {
        final Movie foundMovie = validateIfNotFoundMovie(movieId);
        foundMovie.setRatings(movieRatingProvider.getByTitle(foundMovie.getTitle()));
        foundMovie.calculateRatingsAverage();

        return foundMovie;
    }

    @Override
    public Movie updateById(final Long movieId, final Movie movieToUpdate) {
        validateIfNotFoundMovie(movieId);
        validateReleaseYear(movieToUpdate);

        movieToUpdate.setUpdateDateTime(LocalDateTime.now());
        return movieProvider.updateById(movieId, movieToUpdate);
    }

    @Override
    public Movie rateById(final Long movieId, final Movie movieToRate) {
        movieToRate.getRating().setSource(appInfoProvider.getAppName());
        return movieProvider.rateById(movieId, movieToRate);
    }

    @Override
    public void deleteById(final Long movieId) {
        validateIfNotFoundMovie(movieId);
        movieProvider.deleteById(movieId);
    }

    private Movie validateIfNotFoundMovie(final Long movieId) {
        final Optional<Movie> movieOptional = movieProvider.findById(movieId);
        if (movieOptional.isEmpty()) {
            throw new ResourceNotFoundException(messageProvider.getMessage("notFound.movie.id", movieId));
        }

        return movieOptional.get();
    }

    private void validateIfExistingMovie(final String isan) {
        final Optional<Movie> movieOptional = movieProvider.findByIsan(isan);
        if (movieOptional.isPresent()) {
            throw new ExistingResourceException(messageProvider.getMessage("existing.movie.isan", isan));
        }
    }

    private void validateReleaseYear(final Movie movie) {
        if (movie.isReleaseYearInFuture()) {
            throw new BusinessException(messageProvider.getMessage("releaseYearInFuture.movie.releaseYear"));
        }

        if (movie.isReleaseYearBeforeFirstMovieYear()) {
            throw new BusinessException(messageProvider.getMessage("releaseYearBefore1888.movie.releaseYear"));
        }
    }
}