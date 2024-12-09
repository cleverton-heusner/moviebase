package cleverton.heusner.fixture.dataset.shared;

import jakarta.enterprise.context.Dependent;
import jakarta.persistence.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Dependent
public class EntityMetadataReader {

    public String getTableName(final Object...entities) {
        return getTableName(getEntitiesClass(entities));
    }

    public String getTableName(final Class<?> entityClass) {
        return entityClass.isAnnotationPresent(Table.class) ?
                entityClass.getAnnotation(Table.class).name() :
                entityClass.getSimpleName();
    }

    public String getColumnsNames(final Object...entities) {
        return getPersistentFields(entities).stream()
                .map(field -> field.isAnnotationPresent(Column.class) ?
                        field.getAnnotation(Column.class).name() :
                        field.getName()
                )
                .collect(Collectors.joining(", "));
    }

    public List<Field> getPersistentFields(final Object...entities) {
        return getPersistentFields(getEntitiesClass(entities));
    }

    public List<Field> getPersistentFields(final Class<?> entityClass) {
        return Stream.concat(getChildFields(entityClass), getDescendantFields(entityClass))
                .toList();
    }

    private Class<?> getEntitiesClass(final Object...entities) {
        return entities[0].getClass();
    }

    private Stream<Field> getChildFields(final Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
                .filter(this::isChildField);
    }

    private boolean isChildField(final Field field) {
        return isFieldAnnotatedWithColumn(field) ||
                isFieldNotAnnotated(field) ||
                isPrimaryKeyGeneratedManually(field);
    }

    private boolean isFieldAnnotatedWithColumn(final Field field) {
        return field.isAnnotationPresent(Column.class) && !field.isAnnotationPresent(Id.class);
    }

    private boolean isFieldNotAnnotated(final Field field) {
        return !field.isAnnotationPresent(Id.class) &&
                !field.isAnnotationPresent(Transient.class) &&
                !field.isAnnotationPresent(Embedded.class);
    }

    private boolean isPrimaryKeyGeneratedManually(final Field field) {
        if (field.isAnnotationPresent(GeneratedValue.class)) {
            return switch (field.getAnnotation(GeneratedValue.class).strategy()) {
                case SEQUENCE, AUTO, TABLE -> true;
                default -> false;
            };
        }

        return false;
    }

    public Stream<Field> getDescendantFields(final Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Embedded.class))
                .map(field -> getPersistentFields(field.getType()))
                .flatMap(List::stream);
    }
}