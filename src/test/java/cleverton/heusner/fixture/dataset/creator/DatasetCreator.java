package cleverton.heusner.fixture.dataset.creator;

import cleverton.heusner.fixture.dataset.idgenerator.IdGenerator;
import cleverton.heusner.fixture.dataset.shared.EntityMetadataReader;
import jakarta.enterprise.context.Dependent;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Dependent
public class DatasetCreator {

    private static final String COLUMNS_SEPARATOR = ",";
    private static final String INSERTION_SQL = "INSERT INTO %s (%s) VALUES (%s)";
    private static final String PARAMETER_PLACEHOLDER = "?";
    private static final String PARAMETERS_SEPARATOR = ", ";

    private final DataSource dataSource;
    private final IdGenerator idGenerator;
    private final EntityMetadataReader entityMetadataReader;

    public DatasetCreator(final DataSource dataSource,
                          final IdGenerator idGenerator,
                          final EntityMetadataReader entityMetadataReader) {
        this.dataSource = dataSource;
        this.idGenerator = idGenerator;
        this.entityMetadataReader = entityMetadataReader;
    }

    public final void create(final Object... entities) {
        try (final Connection conn = dataSource.getConnection()) {
            try (final PreparedStatement stmt = conn.prepareStatement(createInsertionSql(entities))) {
                Arrays.stream(entities)
                        .forEach(entity -> populateStatement(stmt, entity, conn));
                stmt.executeBatch();
            }
        } catch (final SQLException e) {
            throw new DatasetCreationException(e);
        }
    }

    private String createInsertionSql(final Object... entities) {
        final String columnsNames = entityMetadataReader.getColumnsNames(entities);
        return String.format(
                INSERTION_SQL,
                entityMetadataReader.getTableName(entities),
                columnsNames,
                getParameters(columnsNames)
        );
    }

    private String getParameters(final String columnsNames) {
        return Arrays.stream(columnsNames.split(COLUMNS_SEPARATOR))
                .map(column -> PARAMETER_PLACEHOLDER)
                .collect(Collectors.joining(PARAMETERS_SEPARATOR));
    }

    private void populateStatement(final PreparedStatement stmt, final Object entity, final Connection conn) {
        final List<Field> persistentFields = entityMetadataReader.getPersistentFields(entity);
        IntStream.range(0, persistentFields.size())
                .forEach(i -> {
                    final Field persistentField = persistentFields.get(i);
                    final long id = idGenerator.generate(persistentField, conn);
                    try {
                        stmt.setObject(++i, id > 0 ? id : getFieldValue(persistentField, entity));
                    }
                    catch (final SQLException e) {
                        throw new DatasetCreationException(String.format(
                                "Failed to set value for field '%s' in SQL statement. Error: ",
                                persistentField.getName()
                        ), e);
                    }
                });
        try {
            stmt.addBatch();
        } catch (final SQLException e) {
            throw new DatasetCreationException("Failed to add batch to statement. Error: ", e);
        }
    }

    private Object getFieldValue(final Field field, final Object entity) {
        field.setAccessible(true);
        try {
            return field.get(getFieldParent(field, entity));
        } catch (final IllegalAccessException e) {
            throw new DatasetCreationException(String.format(
                    "Failed to access value from field %s. Error: ",
                    field.getName()
            ), e);
        }
    }

    private Object getFieldParent(final Field field, final Object currentParentField) {
        if (field.getDeclaringClass() == currentParentField.getClass()) {
            return currentParentField;
        }

        final Field embeddedField = Arrays.stream(currentParentField.getClass().getDeclaredFields())
                .filter(f -> f.getType().equals(field.getDeclaringClass()))
                .findFirst()
                .orElseThrow(() -> new DatasetCreationException(
                        "No matching field found for: " + field.getDeclaringClass())
                );

        final Object nextParentField = getNextParentField(embeddedField, currentParentField);

        try {
            embeddedField.set(currentParentField, nextParentField);
            return getFieldParent(field, nextParentField);
        } catch (final IllegalAccessException e) {
            throw new DatasetCreationException("Failed to initialize parent field.", e);
        }
    }

    private Object getNextParentField(final Field field, final Object currentParentField) {
        try {
            field.setAccessible(true);
            return field.get(currentParentField) == null ?
                    field.getType().getDeclaredConstructor().newInstance() :
                    field.get(currentParentField);
        } catch (final ReflectiveOperationException e) {
            throw new DatasetCreationException("Failed to access parent field.", e);
        }
    }
}