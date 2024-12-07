package cleverton.heusner.fixture;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

import javax.sql.DataSource;

@Dependent
public class DatasetManagerConfiguration<MovieEntity> {

    @Produces
    public DatasetManager dataSource(final DataSource dataSource) {
        return new DatasetManager(dataSource);
    }
}