package cleverton.heusner.fixture.dataset.cleaner;

import cleverton.heusner.fixture.dataset.shared.EntityMetadataReader;
import jakarta.enterprise.context.Dependent;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

@Dependent
public class DatasetCleaner {

    private static final String TABLE_CLEANING_SQL = "TRUNCATE TABLE %s RESTART IDENTITY CASCADE";

    private final DataSource dataSource;
    private final EntityMetadataReader entityMetadataReader;

    public DatasetCleaner(final DataSource dataSource, final EntityMetadataReader entityMetadataReader) {
        this.dataSource = dataSource;
        this.entityMetadataReader = entityMetadataReader;
    }

    public void clean(final Class<?>... entityClasses) {
        try (final Connection conn = dataSource.getConnection();
             final Statement stmt = conn.createStatement()) {
            Arrays.stream(entityClasses)
                    .forEach(entityClass ->
                    {
                        final String tableName = entityMetadataReader.getTableName(entityClass);
                        try {
                            stmt.addBatch(String.format(TABLE_CLEANING_SQL, tableName));
                        } catch (final SQLException e) {
                            throw new DatasetCleaningException(
                                    String.format("Failed to clean table %s. Error: ", tableName),
                                    e
                            );
                        }
                    }
            );
            stmt.executeBatch();
        } catch (final SQLException e) {
            throw new DatasetCleaningException("Failed to clean tables. Error: ", e);
        }
    }
}