package cleverton.heusner.port.output;

import cleverton.heusner.domain.model.Movie;

import java.util.List;

public interface MovieRatingProvider {
    List<Movie.Rating> getByTitle(final String title);
}
