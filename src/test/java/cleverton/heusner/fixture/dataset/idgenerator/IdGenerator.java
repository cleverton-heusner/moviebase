package cleverton.heusner.fixture.dataset.idgenerator;

import cleverton.heusner.fixture.dataset.shared.EntityMetadataReader;
import jakarta.enterprise.context.Dependent;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@Dependent
public class IdGenerator {

    private static final int ID_COLUMN_INDEX = 1;

    private final IdGenerationQuerySelector idGenerationQuerySelector;
    private final EntityMetadataReader entityMetadataReader;

    public IdGenerator(final IdGenerationQuerySelector idGenerationQuerySelector,
                       final EntityMetadataReader entityMetadataReader) {
        this.idGenerationQuerySelector = idGenerationQuerySelector;
        this.entityMetadataReader = entityMetadataReader;
    }

    public long generate(final Field idField, final Connection connection) {
        final Optional<String> queryOptional = idGenerationQuerySelector.selectQuery(idField);
        return queryOptional.map(query -> generateId(connection, query, idField.getName()))
                .orElse(0L);
    }

    private long generateId(final Connection connection, final String query, final String fieldName) {
        try (final Statement statement = connection.createStatement()) {
            final ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getLong(ID_COLUMN_INDEX);
            }
        } catch (final SQLException e) {
            throw new IdGenerationException(
                    String.format("Failed to generate ID for field %s. Error: ", fieldName),
                    e
            );
        }

        return 0;
    }
}