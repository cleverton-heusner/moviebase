package cleverton.heusner.fixture.dataset.cleaner;

import cleverton.heusner.fixture.dataset.shared.EntityMetadataReader;
import jakarta.enterprise.context.Dependent;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Dependent
public class DatasetCleaner {

    private final DataSource dataSource;
    private final EntityMetadataReader entityMetadataReader;

    public DatasetCleaner(final DataSource dataSource, final EntityMetadataReader entityMetadataReader) {
        this.dataSource = dataSource;
        this.entityMetadataReader = entityMetadataReader;
    }

    public void clean(final Class<?> entityClass) {
        final String tableName = entityMetadataReader.getTableName(entityClass);
        try (final Connection connection = dataSource.getConnection()) {
            final Statement stmt = connection.createStatement();
            stmt.execute("TRUNCATE TABLE " + tableName + " RESTART IDENTITY CASCADE");
        } catch (final SQLException e) {
            throw new RuntimeException(String.format("Failed to clean dataset in table %s. Error: ", tableName), e);
        }
    }
}