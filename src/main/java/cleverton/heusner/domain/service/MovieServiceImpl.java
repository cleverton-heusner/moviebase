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
import cleverton.heusner.port.shared.LoggerService;

import java.util.List;
import java.util.Optional;

public class MovieServiceImpl implements MovieService {

    private final MovieProvider movieProvider;
    private final MovieRatingProvider movieRatingProvider;
    private final AppInfoProvider appInfoProvider;
    private final MessageProvider messageProvider;
    private final LoggerService logger;

    public MovieServiceImpl(final MovieProvider movieProvider,
                            final MovieRatingProvider movieRatingProvider,
                            final AppInfoProvider appInfoProvider,
                            final MessageProvider messageProvider,
                            final LoggerService logger) {
        this.movieProvider = movieProvider;
        this.movieRatingProvider = movieRatingProvider;
        this.appInfoProvider = appInfoProvider;
        this.messageProvider = messageProvider;
        this.logger = logger;
    }

    @Override
    public Movie create(final Movie movie) {
        logger.info("Creating movie with title '%' and ISAN '%'.", movie.getTitle(), movie.getIsan());

        validateIfExistingMovie(movie.getIsan());
        validateReleaseYear(movie, Operation.CREATE);

        return movieProvider.create(movie.createdAtNow());
    }

    @Override
    public Long countMovies() {
        logger.info("Counting all created movies.");
        return movieProvider.countMovies();
    }

    @Override
    public List<Movie> list() {
        logger.info("Listing all created movies.");
        return movieProvider.list();
    }

    @Override
    public Movie getRatingsById(final Long movieId) {
        logger.info("Retrieving ratings of movie by ID '%'.", movieId);

        final Movie foundMovie = validateIfNotFoundMovie(movieId);
        return foundMovie.withRatings(movieRatingProvider.getByTitle(foundMovie.getTitle()))
                .withRatingsAverage();
    }

    @Override
    public Movie updateById(final Long movieId, final Movie movieToUpdate) {
        logger.info("Updating movie by ID '%'.", movieId);

        validateIfNotFoundMovie(movieId);
        validateReleaseYear(movieToUpdate, Operation.UPDATE);

        return movieProvider.updateById(movieId, movieToUpdate.updatedAtNow());
    }

    @Override
    public Movie rateById(final Long movieId, final Movie movieToRate) {
        logger.info("Rating movie by ID '%' with value '%'.", movieId);

        final Movie movie = validateIfNotFoundMovie(movieId);
        final var rating = new Movie.Rating(appInfoProvider.getAppName(), movieToRate.getRating().getValue());
        return movieProvider.rateById(movieId, movie.withRating(rating));
    }

    @Override
    public void deleteById(final Long movieId) {
        logger.info("Deleting movie by ID '%'.", movieId);
        validateIfNotFoundMovie(movieId);
        movieProvider.deleteById(movieId);
    }

    private Movie validateIfNotFoundMovie(final Long movieId) {
        logger.info("Validating existence of movie by ID '%'.", movieId);

        final Optional<Movie> movieOptional = movieProvider.findById(movieId);
        if (movieOptional.isEmpty()) {
            logger.warn("Movie with ID '%' not found.", movieId);
            throw new ResourceNotFoundException(messageProvider.getMessage("notFound.movie.id", movieId));
        }

        return movieOptional.get();
    }

    private void validateIfExistingMovie(final String isan) {
        logger.info("Validating existence of movie by ISAN '%'.", isan);

        final Optional<Movie> movieOptional = movieProvider.findByIsan(isan);
        if (movieOptional.isPresent()) {
            logger.warn("Cannot create movie. Reason: movie with ISAN '%' already existing.", isan);
            throw new ExistingResourceException(messageProvider.getMessage("existing.movie.isan", isan));
        }
    }

    private void validateReleaseYear(final Movie movie, final Operation operation) {
        logger.info("Validating release year '%' of movie with ID '%'.", movie.getReleaseYear(), movie.getId());
        if (movie.isReleaseYearInFuture()) {
            logger.warn(
                    "Cannot '%' movie with ID '%' and release year '%'. Reason: release year cannot be in " +
                            "the future.",
                    operation.getValue(),
                    movie.getId(),
                    movie.getReleaseYear()
            );
            throw new BusinessException(messageProvider.getMessage("releaseYearInFuture.movie.releaseYear"));
        }

        if (movie.isReleaseYearBeforeFirstMovieYear()) {
            logger.warn(
                    "Cannot '%' movie with ID '%' and release year '%'. Reason: release year cannot be " +
                            "before 1888.",
                    operation.getValue(),
                    movie.getId(),
                    movie.getReleaseYear()
            );
            throw new BusinessException(messageProvider.getMessage("releaseYearBefore1888.movie.releaseYear"));
        }
    }

    public enum Operation {
        CREATE("create"),
        UPDATE("update");

        private final String value;

        Operation(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}