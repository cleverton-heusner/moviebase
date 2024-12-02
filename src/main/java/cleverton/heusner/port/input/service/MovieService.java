package cleverton.heusner.port.input.service;

import cleverton.heusner.domain.model.Movie;

import java.util.List;

public interface MovieService {
    Movie create(final Movie movie);
    Long countMovies();
    List<Movie> list();
    Movie getRatingsById(final Long movieId);
    Movie updateById(final Long movieId, final Movie movieToUpdate);
    Movie rateById(final Long movieId, final Movie movieToRate);
    void deleteById(final Long movieId);
}