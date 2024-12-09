package cleverton.heusner.fixture.dataset.idgenerator;

import cleverton.heusner.fixture.dataset.shared.EntityMetadataReader;
import jakarta.enterprise.context.Dependent;

import java.lang.reflect.Field;
import java.util.Optional;

@Dependent
public class SequenceGeneratorQueryCreator {

    private final EntityMetadataReader entityMetadataReader;

    public SequenceGeneratorQueryCreator(final EntityMetadataReader entityMetadataReader) {
        this.entityMetadataReader = entityMetadataReader;
    }

    public Optional<String> createQuery(final Field idField) {
        return Optional.of(String.format(
                "SELECT nextval('%s')",
                entityMetadataReader.getGenerator(idField))
        );
    }
}