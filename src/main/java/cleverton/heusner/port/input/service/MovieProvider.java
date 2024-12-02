package cleverton.heusner.port.input.service;

import cleverton.heusner.domain.model.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieProvider {
    Movie create(final Movie movie);
    Long countMovies();
    List<Movie> list();
    Optional<Movie> findById(final Long movieId);
    Optional<Movie> findByIsan(final String isan);
    Movie updateById(final Long movieId, final Movie movieToUpdate);
    Movie rateById(final Long movieId, final Movie movieToRate);
    void deleteById(final Long movieId);
}
