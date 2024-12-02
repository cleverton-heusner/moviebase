package cleverton.heusner.adapter.output.repository;

import cleverton.heusner.adapter.output.entity.MovieEntity;
import cleverton.heusner.domain.model.Movie;
import cleverton.heusner.port.output.MovieRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.RequestScoped;

import java.util.Optional;

@RequestScoped
public class MovieRepositoryImpl implements MovieRepository {

    @Override
    public Optional<MovieEntity> findByIsanOptional(final String isan) {
        return find(ISAN_FIELD, isan).singleResultOptional();
    }

    @Override
    public void updateById(final Long movieId, final Movie movieToUpdate) {
        final String updateMovieByIdQuery = """
                %s =: %s,
                %s =: %s,
                %s =: %s
                where %s =: %s
                """.formatted(
                TITLE_FIELD, TITLE_PARAM,
                RELEASE_YEAR_FIELD, RELEASE_YEAR_PARAM,
                UPDATE_DATE_TIME_FIELD, UPDATE_DATE_TIME_PARAM,
                ID_FIELD, ID_PARAM
        );

        update(
                updateMovieByIdQuery,
                Parameters.with(TITLE_PARAM, movieToUpdate.getTitle())
                        .and(RELEASE_YEAR_PARAM, movieToUpdate.getReleaseYear())
                        .and(UPDATE_DATE_TIME_PARAM, movieToUpdate.getUpdateDateTime())
                        .and(ID_PARAM, movieId)
        );
    }

    @Override
    public void rateById(final Long movieId, final Movie movieToRate) {
        final String rateMovieByIdQuery = """
                %s =: %s,
                %s =: %s
                where %s =: %s
                """.formatted(
                RATING_SOURCE_FIELD, RATING_SOURCE_PARAM,
                RATING_VALUE_FIELD, RATING_VALUE_PARAM,
                ID_FIELD, ID_PARAM
        );

        update(
                rateMovieByIdQuery,
                Parameters.with(RATING_SOURCE_PARAM, movieToRate.getRating().getSource())
                        .and(RATING_VALUE_PARAM, movieToRate.getRating().getValue())
                        .and(ID_PARAM, movieId)
        );
    }
}