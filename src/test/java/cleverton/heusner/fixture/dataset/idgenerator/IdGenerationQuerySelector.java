package cleverton.heusner.fixture.dataset.idgenerator;

import cleverton.heusner.fixture.dataset.shared.EntityMetadataReader;
import jakarta.enterprise.context.Dependent;
import jakarta.persistence.GeneratedValue;

import java.lang.reflect.Field;
import java.util.Optional;

import static jakarta.persistence.GenerationType.*;

@Dependent
public class IdGenerationQuerySelector {

    private final EntityMetadataReader entityMetadataReader;
    private final SequenceGeneratorQueryCreator sequenceGeneratorQueryCreator;
    private final TableGeneratorQueryCreator tableGeneratorQueryCreator;

    public IdGenerationQuerySelector(final EntityMetadataReader entityMetadataReader,
                                     final SequenceGeneratorQueryCreator sequenceGeneratorQueryCreator,
                                     final TableGeneratorQueryCreator tableGeneratorQueryCreator) {
        this.entityMetadataReader = entityMetadataReader;
        this.sequenceGeneratorQueryCreator = sequenceGeneratorQueryCreator;
        this.tableGeneratorQueryCreator = tableGeneratorQueryCreator;
    }

    public Optional<String> selectQuery(final Field idField) {
        if (idField.isAnnotationPresent(GeneratedValue.class)) {
            final String generator = entityMetadataReader.getGenerator(idField);
            final String strategyName = entityMetadataReader.getGeneratorStrategyName(idField);

            validGenerator(generator, idField);

            if (SEQUENCE.name().equals(strategyName) || AUTO.name().equals(strategyName)) {
                return sequenceGeneratorQueryCreator.createQuery(idField);
            }
            else if (TABLE.name().equals(strategyName)) {
                return tableGeneratorQueryCreator.createQuery(idField);
            }
        }

        return Optional.empty();
    }

    private void validGenerator(final String idGenerator, final Field field) {
        if (idGenerator.isBlank()) {
            throw new IdGenerationException(String.format(
                    "Generator not provided for table %s.",
                    entityMetadataReader.getTableName(field)
            ));
        }
    }
}