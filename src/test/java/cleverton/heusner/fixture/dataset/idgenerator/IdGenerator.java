package cleverton.heusner.fixture.dataset.idgenerator;

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

    public IdGenerator(final IdGenerationQuerySelector idGenerationQuerySelector) {
        this.idGenerationQuerySelector = idGenerationQuerySelector;
    }

    public long generate(final Field field, final Connection connection) {
        final Optional<String> queryOptional = idGenerationQuerySelector.selectQuery(field);
        return queryOptional.map(query -> generateId(connection, query, field.getName()))
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