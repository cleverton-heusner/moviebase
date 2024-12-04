package cleverton.heusner.adapter.output.repository;

import cleverton.heusner.adapter.output.entity.MovieEntity;
import cleverton.heusner.port.output.MovieRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.inject.Inject;
import jakarta.persistence.Table;

public class RepositoryImplTest {

    @Inject
    protected PanacheRepository<MovieEntity> datasetManager;

    @Inject
    protected MovieRepository movieRepository;

    protected void resetMovieId() {
        datasetManager.getEntityManager()
                .createNativeQuery(getNativeQuery())
                .executeUpdate();
    }

    private String getNativeQuery() {
        return "TRUNCATE TABLE " + getMovieTableName() + " RESTART IDENTITY CASCADE";
    }

    private String getMovieTableName() {
        return MovieEntity.class.getAnnotation(Table.class).name();
    }
}