package cleverton.heusner.fixture.dataset.idgenerator;

import jakarta.enterprise.context.Dependent;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.TableGenerator;

import java.lang.reflect.Field;
import java.util.Optional;

import static jakarta.persistence.GenerationType.*;

@Dependent
public class IdGenerationQuerySelector {

    public Optional<String> selectQuery(final Field field) {
        if (field.isAnnotationPresent(GeneratedValue.class)) {
            final GeneratedValue generatedValueAnnotation = field.getAnnotation(GeneratedValue.class);
            final String idGenerationStrategyName = generatedValueAnnotation.strategy().name();

            if (SEQUENCE.name().equals(idGenerationStrategyName) || AUTO.name().equals(idGenerationStrategyName)) {
                final String idGeneratorSequence = generatedValueAnnotation.generator();
                return Optional.of("SELECT nextval('" + idGeneratorSequence + "')");
            }
            else if (TABLE.name().equals(idGenerationStrategyName)) {
                final String idGeneratorTable = generatedValueAnnotation.generator();
                final TableGenerator tableGeneratorAnnotation = field.getAnnotation(TableGenerator.class);
                final String entityPkColumnName = tableGeneratorAnnotation.pkColumnName();
                final String currentIdColumnName = tableGeneratorAnnotation.valueColumnName();
                return Optional.of("""
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
                ));
            }
        }

        return Optional.empty();
    }
}