package cleverton.heusner.port.output;

import cleverton.heusner.adapter.output.entity.MovieEntity;
import cleverton.heusner.domain.model.Movie;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.Optional;

public interface MovieRepository extends PanacheRepository<MovieEntity> {

    String ID_FIELD = "id";
    String ID_PARAM = "idParam";
    String ISAN_FIELD = "isan";
    String RATING_SOURCE_FIELD = "ratingEntity.source";
    String RATING_SOURCE_PARAM = "sourceParam";
    String RATING_VALUE_FIELD = "ratingEntity.value";
    String RATING_VALUE_PARAM = "valueParam";
    String RELEASE_YEAR_FIELD = "releaseYear";
    String RELEASE_YEAR_PARAM = "releaseYearParam";
    String TITLE_FIELD = "title";
    String TITLE_PARAM = "titleParam";
    String UPDATE_DATE_TIME_FIELD = "updateDateTime";
    String UPDATE_DATE_TIME_PARAM = "updateDateTimeParam";

    Optional<MovieEntity> findByIsanOptional(final String isan);
    void updateById(final Long movieId, final Movie movieToUpdate);
    void rateById(final Long movieId, final Movie movieToRate);
}