package cleverton.heusner.fixture.dataset.idgenerator;

import cleverton.heusner.fixture.dataset.shared.EntityMetadataReader;
import jakarta.enterprise.context.Dependent;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.TableGenerator;

import java.lang.reflect.Field;
import java.util.Optional;

import static jakarta.persistence.GenerationType.*;

@Dependent
public class IdGenerationQuerySelector {

    private final EntityMetadataReader entityMetadataReader;

    public IdGenerationQuerySelector(final EntityMetadataReader entityMetadataReader) {
        this.entityMetadataReader = entityMetadataReader;
    }

    public Optional<String> selectQuery(final Field field) {
        if (field.isAnnotationPresent(GeneratedValue.class)) {
            final var generatedValueAnnotation = field.getAnnotation(GeneratedValue.class);
            final String idGenerationStrategyName = generatedValueAnnotation.strategy().name();
            final String idGenerator = generatedValueAnnotation.generator();

            validIdGenerator(idGenerator, field);

            if (SEQUENCE.name().equals(idGenerationStrategyName) || AUTO.name().equals(idGenerationStrategyName)) {
                return Optional.of(String.format("SELECT nextval('%s')", idGenerator));
            }
            else if (TABLE.name().equals(idGenerationStrategyName)) {
                final var generatorAnnotation = field.getAnnotation(TableGenerator.class);
                final String entityPkColumnName = generatorAnnotation.pkColumnName();
                final String currentIdColumnName = generatorAnnotation.valueColumnName();
                return Optional.of(String.format(
                        "UPDATE %s SET %s = %s + 1 WHERE %s = '%s' RETURNING %s",
                        idGenerator,
                        currentIdColumnName,
                        currentIdColumnName,
                        entityPkColumnName,
                        entityPkColumnName,
                        currentIdColumnName));
            }
        }

        return Optional.empty();
    }

    private void validIdGenerator(final String idGenerator, final Field field) {
        if (idGenerator.isBlank()) {
            throw new IdGenerationException(String.format(
                    "Generator not provided for table %s.",
                    entityMetadataReader.getTableName(field)
            ));
        }
    }
}