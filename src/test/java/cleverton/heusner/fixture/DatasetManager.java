package cleverton.heusner.fixture;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Table;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DatasetManager<T> {

    private final DataSource dataSource;

    public DatasetManager(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @SafeVarargs
    public final void create(final T... entities) throws SQLException {
        try (final Connection connection = dataSource.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(createSql(entities))) {
                Arrays.stream(entities)
                        .forEach(entity -> populateStatement(statement, entity));
                statement.executeBatch();
            }
        }
    }

    @SafeVarargs
    private String createSql(final T... entities) {
        final Class<?> entityClass = entities[0].getClass();
        final String columnsNames = getColumnsNames(entityClass);

        return """
        INSERT INTO %s (%s) VALUES (%s)
        """.formatted(getTableName(entityClass), columnsNames, getParameters(columnsNames));
    }

    private String getTableName(final Class<?> entityClass) {
        return entityClass.isAnnotationPresent(Table.class) ?
                entityClass.getAnnotation(Table.class).name() :
                entityClass.getName();
    }

    private String getColumnsNames(final Class<?> entityClass) {
        return getFieldsMappedToColumns(entityClass).stream()
                .map(field -> field.getAnnotation(Column.class).name())
                .collect(Collectors.joining(", "));
    }

    private String getParameters(final String columnsNames) {
        return Arrays.stream(columnsNames.split(","))
                .map(i -> "?")
                .collect(Collectors.joining(", "));
    }

    private void populateStatement(final PreparedStatement statement, final T entity) {
        final List<Field> fieldsMappedToColumns = getFieldsMappedToColumns(entity.getClass());
        IntStream.range(0, fieldsMappedToColumns.size())
                .forEach(i -> {
                    final Field field = fieldsMappedToColumns.get(i);
                    field.setAccessible(true);
                    try {
                        statement.setObject(i + 1, field.get(getFieldContext(field, entity)));
                    } catch (final IllegalAccessException e) {
                        throw new RuntimeException(String.format(
                                "Failed to access value from field %s. Error: ",
                                field.getName()
                        ), e);
                    } catch (final SQLException e) {
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
                .filter(field -> field.isAnnotationPresent(Column.class));
    }

    private Stream<Field> getDescendantFields(final Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Embedded.class))
                .map(field -> getFieldsMappedToColumns(field.getType()))
                .flatMap(List::stream);
    }

    private Object getFieldContext(final Field field, final Object currentContext) {
        if (field.getDeclaringClass() == currentContext.getClass()) {
            return currentContext;
        }

        final Field embeddedField = Arrays.stream(currentContext.getClass().getDeclaredFields())
                .filter(f -> f.getType().equals(field.getDeclaringClass()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No matching field found for: " + field.getDeclaringClass()));

        embeddedField.setAccessible(true);

        try {
            Object nextContext = embeddedField.get(currentContext);
            if (nextContext == null) {
                nextContext = embeddedField.getType().getDeclaredConstructor().newInstance();
                embeddedField.set(currentContext, nextContext);
            }

            return getFieldContext(field, nextContext);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException("Failed to access or initialize context", e);
        }
    }

    public void clean(final Class<T> entityClass) {
        final String tableName = getTableName(entityClass);
        try (final Connection connection = dataSource.getConnection()) {
            final Statement stmt = connection.createStatement();
            stmt.execute("TRUNCATE TABLE " + tableName + " RESTART IDENTITY CASCADE");
        } catch (final SQLException e) {
            throw new RuntimeException(String.format("Failed to clean dataset in table %s. Error: ", tableName), e);
        }
    }
}