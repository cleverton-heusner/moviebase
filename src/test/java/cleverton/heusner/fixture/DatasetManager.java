package cleverton.heusner.fixture;

import jakarta.persistence.*;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static jakarta.persistence.GenerationType.SEQUENCE;
import static jakarta.persistence.GenerationType.TABLE;

public class DatasetManager {

    private final DataSource dataSource;

    public DatasetManager(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public final void create(final Object... entities) throws SQLException {
        try (final Connection connection = dataSource.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(createSql(entities))) {
                Arrays.stream(entities)
                        .forEach(entity -> populateStatement(statement, entity, connection));
                statement.executeBatch();
            }
        }
    }

    private String createSql(final Object... entities) {
        final Class<?> entityClass = entities[0].getClass();
        final String columnsNames = getColumnsNames(entityClass);

        return """
        INSERT INTO %s (%s) VALUES (%s)
        """.formatted(getTableName(entityClass), columnsNames, getParameters(columnsNames));
    }

    private String getTableName(final Class<?> entityClass) {
        return entityClass.isAnnotationPresent(Table.class) ?
                entityClass.getAnnotation(Table.class).name() :
                entityClass.getSimpleName();
    }

    private String getColumnsNames(final Class<?> entityClass) {
        return getFieldsMappedToColumns(entityClass).stream()
                .map(field -> field.isAnnotationPresent(Column.class) ?
                        field.getAnnotation(Column.class).name() :
                        field.getName()
                )
                .collect(Collectors.joining(", "));
    }

    private String getParameters(final String columnsNames) {
        return Arrays.stream(columnsNames.split(","))
                .map(i -> "?")
                .collect(Collectors.joining(", "));
    }

    private void populateStatement(final PreparedStatement statement, final Object entity, final Connection connection) {
        final List<Field> fieldsMappedToColumns = getFieldsMappedToColumns(entity.getClass());
        IntStream.range(0, fieldsMappedToColumns.size())
                .forEach(i -> {
                    final Field field = fieldsMappedToColumns.get(i);
                    final long sequence = generateSequenceForIdField(field, connection);
                    try {
                        statement.setObject(++i, sequence > 0 ? sequence : getFieldValue(field, entity));
                    }
                    catch (final SQLException e) {
                        throw new RuntimeException(String.format(
                                "Failed to set value for field '%s' in SQL statement. Error: ",
                                field.getName()
                        ), e);
                    }
                });
        try {
            statement.addBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add batch to statement. Error: ", e);
        }
    }

    public List<Field> getFieldsMappedToColumns(final Class<?> entityClass) {
        return Stream.concat(getRootFields(entityClass), getDescendantFields(entityClass))
                .toList();
    }

    private Stream<Field> getRootFields(final Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
                .filter(field ->
                        isFieldAnnotatedWithColumn(field) ||
                        isIdFieldGeneratedBySequence(field) ||
                        isIdFieldGeneratedByTable(field) ||
                        isFieldNotAnnotated(field)
                );
    }

    private boolean isFieldAnnotatedWithColumn(final Field field) {
        return field.isAnnotationPresent(Column.class);
    }

    private boolean isIdFieldGeneratedBySequence(final Field field) {
        return field.isAnnotationPresent(GeneratedValue.class) &&
                SEQUENCE.name()
                        .equals(
                                field.getAnnotation(GeneratedValue.class)
                                        .strategy()
                                        .name()
                        );
    }

    private boolean isIdFieldGeneratedByTable(final Field field) {
        return field.isAnnotationPresent(GeneratedValue.class) &&
                TABLE.name()
                        .equals(
                                field.getAnnotation(GeneratedValue.class)
                                        .strategy()
                                        .name()
                        );
    }

    private boolean isFieldNotAnnotated(final Field field) {
        return !field.isAnnotationPresent(Id.class) &&
                !field.isAnnotationPresent(Transient.class) &&
                !field.isAnnotationPresent(Embedded.class);
    }

    private Stream<Field> getDescendantFields(final Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Embedded.class))
                .map(field -> getFieldsMappedToColumns(field.getType()))
                .flatMap(List::stream);
    }

    private Object getFieldValue(final Field field, final Object entity) {
        field.setAccessible(true);
        try {
            return field.get(getFieldParent(field, entity));
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(String.format(
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
                .orElseThrow(() -> new IllegalArgumentException(
                        "No matching field found for: " + field.getDeclaringClass())
                );

        final Object nextParentField = getNextParentField(embeddedField, currentParentField);

        try {
            embeddedField.set(currentParentField, nextParentField);
            return getFieldParent(field, nextParentField);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException("Failed to initialize parent field.", e);
        }
    }

    private Object getNextParentField(final Field field, final Object currentParentField) {
        try {
            field.setAccessible(true);
            return field.get(currentParentField) == null ?
                    field.getType().getDeclaredConstructor().newInstance() :
                    field.get(currentParentField);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException("Failed to access parent field.", e);
        }
    }

    private long generateSequenceForIdField(final Field field, final Connection connection) {
        if (isIdFieldGeneratedBySequence(field)) {
            final String idGeneratorSequence = field.getAnnotation(GeneratedValue.class)
                    .generator();
            try (final Statement statement = connection.createStatement()) {
                final ResultSet resultSet = statement.executeQuery("SELECT nextval('" + idGeneratorSequence + "')");
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
            } catch (final SQLException e) {
                throw new RuntimeException(
                        String.format("Failed to generate ID for field %s. Error: ", field.getName()),
                        e
                );
            }
        }
        else if (isIdFieldGeneratedByTable(field)) {
            final String idGeneratorTable = field.getAnnotation(GeneratedValue.class).generator();
            final String entityPkColumnName = field.getAnnotation(TableGenerator.class).pkColumnName();
            final String currentIdColumnName = field.getAnnotation(TableGenerator.class).valueColumnName();
            final String query = """
            UPDATE %s
             SET %s = %s + 1
             WHERE %s = '%s'
             RETURNING %s
            """.formatted(
                    idGeneratorTable,
                    currentIdColumnName,
                    currentIdColumnName,
                    entityPkColumnName,
                    entityPkColumnName,
                    currentIdColumnName
            );

            try (final Statement statement = connection.createStatement()) {
                final ResultSet resultSet = statement.executeQuery(query);
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
            } catch (final SQLException e) {
                throw new RuntimeException(
                        String.format("Failed to generate ID for field %s. Error: ", field.getName()),
                        e
                );
            }
        }

        return 0;
    }

    public void clean(final Class<?> entityClass) {
        final String tableName = getTableName(entityClass);
        try (final Connection connection = dataSource.getConnection()) {
            final Statement stmt = connection.createStatement();
            stmt.execute("TRUNCATE TABLE " + tableName + " RESTART IDENTITY CASCADE");
        } catch (final SQLException e) {
            throw new RuntimeException(String.format("Failed to clean dataset in table %s. Error: ", tableName), e);
        }
    }
}