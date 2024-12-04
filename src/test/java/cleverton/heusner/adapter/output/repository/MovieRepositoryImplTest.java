package cleverton.heusner.adapter.output.repository;

import cleverton.heusner.adapter.output.entity.MovieEntity;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

@QuarkusTest
@TestTransaction
public class MovieRepositoryImplTest extends RepositoryImplTest {

    @Test
    void given_movieIsValid_when_movieIsCreated_then_createdMovieIsRetrieved() {
        // Arrange
        resetMovieId();

        final long expectedMovieId = 1L;
        final var expectedMovieEntity = Instancio.of(MovieEntity.class)
                .set(field(MovieEntity::getId), null)
                .create();

        // Act
        movieRepository.persist(expectedMovieEntity);

        // Assert
        final MovieEntity actualMovieEntity = datasetManager.findById(expectedMovieId);
        assertThat(actualMovieEntity).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedMovieEntity);
        assertThat(actualMovieEntity.getId()).isEqualTo(expectedMovieId);
    }

    @Test
    void given_movieWithNullTitle_when_movieIsCreated_then_constraintViolationExceptionReturned() {
        // Arrange
        final var expectedMovieEntity = Instancio.of(MovieEntity.class)
                .set(field(MovieEntity::getId), null)
                .set(field(MovieEntity::getTitle), null)
                .create();

        // Act & Assert
        assertThatThrownBy(() -> movieRepository.persist(expectedMovieEntity))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void given_movieWithTitleHavingMoreThan40Characters_when_movieIsCreated_then_dataExceptionReturned() {
        // Arrange
        final var expectedMovieEntity = Instancio.of(MovieEntity.class)
                .set(field(MovieEntity::getId), null)
                .set(field(MovieEntity::getTitle), "a".repeat(41))
                .create();

        // Act & Assert
        assertThatThrownBy(() -> movieRepository.persist(expectedMovieEntity))
                .isInstanceOf(DataException.class);

    }

    @Test
    void given_movieWithNullIsan_when_movieIsCreated_then_constraintViolationExceptionReturned() {
        // Arrange
        final var expectedMovieEntity = Instancio.of(MovieEntity.class)
                .set(field(MovieEntity::getId), null)
                .set(field(MovieEntity::getIsan), null)
                .create();

        // Act & Assert
        assertThatThrownBy(() -> movieRepository.persist(expectedMovieEntity))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void given_movieWithIsanHavingMoreThan26Characters_when_movieIsCreated_then_dataExceptionReturned() {
        // Arrange
        final var expectedMovieEntity = Instancio.of(MovieEntity.class)
                .set(field(MovieEntity::getId), null)
                .set(field(MovieEntity::getIsan), "a".repeat(27))
                .create();

        // Act & Assert
        assertThatThrownBy(() -> movieRepository.persist(expectedMovieEntity))
                .isInstanceOf(DataException.class);

    }
}