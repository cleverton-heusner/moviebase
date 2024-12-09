package cleverton.heusner.fixture.dataset.idgenerator;

import cleverton.heusner.fixture.dataset.shared.EntityMetadataReader;
import jakarta.enterprise.context.Dependent;

import java.lang.reflect.Field;
import java.util.Optional;

@Dependent
public class TableGeneratorQueryCreator {

    private final EntityMetadataReader entityMetadataReader;

    public TableGeneratorQueryCreator(final EntityMetadataReader entityMetadataReader) {
        this.entityMetadataReader = entityMetadataReader;
    }

    public Optional<String> createQuery(final Field field) {
        final var tableGenerator = entityMetadataReader.getTableGenerator(field);

        return Optional.of(String.format(
                "UPDATE %s SET %s = %s + 1 WHERE %s = '%s' RETURNING %s",
                tableGenerator.name(),
                tableGenerator.valueColumnName(),
                tableGenerator.valueColumnName(),
                tableGenerator.pkColumnName(),
                tableGenerator.pkColumnName(),
                tableGenerator.valueColumnName()
        ));
    }
}