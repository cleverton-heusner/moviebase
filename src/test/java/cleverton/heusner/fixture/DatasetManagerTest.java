package cleverton.heusner.fixture;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.instancio.Select.field;

@QuarkusTest
public class DatasetManagerTest {

    @Inject
    private DatasetManager datasetManager;

    @AfterEach
    void tearDown() {
        datasetManager.clean(MovieEntityWithColumnAnnotation.class);
        datasetManager.clean(MovieEntityWithoutColumnAnnotation.class);
    }

    @Test
    void test_movieEntityWithColumnAnnotation() throws SQLException {

        // Arrange
        final var now = truncateToMicroseconds(LocalDateTime.now());
        final var expectedMovieDataset = Instancio.of(MovieEntityWithColumnAnnotation.class)
                .set(field(MovieEntityWithColumnAnnotation::getId), null)
                .set(field(MovieEntityWithColumnAnnotation::getCreationDateTime), now)
                .set(field(MovieEntityWithColumnAnnotation::getUpdateDateTime), now)
                .create();

        // Act
        datasetManager.create(expectedMovieDataset);

        // Assert
        expectedMovieDataset.setId(1L);
        final var actualMovieDataset = MovieEntityWithColumnAnnotation.findById(1L);
        assertThat(actualMovieDataset).isEqualTo(expectedMovieDataset);
    }

    @Test
    void test_movieEntityWithColumnAnnotationInId() throws SQLException {

        // Arrange
        final var now = truncateToMicroseconds(LocalDateTime.now());
        final var expectedMovieDataset = Instancio.of(MovieEntityWithColumnAnnotationInId.class)
                .set(field(MovieEntityWithColumnAnnotationInId::getId), null)
                .set(field(MovieEntityWithColumnAnnotationInId::getCreationDateTime), now)
                .set(field(MovieEntityWithColumnAnnotationInId::getUpdateDateTime), now)
                .create();

        // Act
        datasetManager.create(expectedMovieDataset);

        // Assert
        expectedMovieDataset.setId(1L);
        final var actualMovieDataset = MovieEntityWithColumnAnnotationInId.findById(1L);
        assertThat(actualMovieDataset).isEqualTo(expectedMovieDataset);
    }

    @Test
    void test_movieEntityWithoutColumnAnnotation() throws SQLException {

        // Arrange
        final var now = truncateToMicroseconds(LocalDateTime.now());
        final var expectedMovieDataset = Instancio.of(MovieEntityWithoutColumnAnnotation.class)
                .set(field(MovieEntityWithoutColumnAnnotation::getId), null)
                .set(field(MovieEntityWithoutColumnAnnotation::getCreationDateTime), now)
                .set(field(MovieEntityWithoutColumnAnnotation::getUpdateDateTime), now)
                .create();

        // Act
        datasetManager.create(expectedMovieDataset);

        // Assert
        expectedMovieDataset.setId(1L);
        final var actualMovieDataset = MovieEntityWithoutColumnAnnotation.findById(1L);
        assertThat(actualMovieDataset).isEqualTo(expectedMovieDataset);
    }

    @Test
    void test_movieEntityWithTransientAnnotation() throws SQLException {

        // Arrange
        final var now = truncateToMicroseconds(LocalDateTime.now());
        final var expectedMovieDataset = Instancio.of(MovieEntityWithTransientAnnotation.class)
                .set(field(MovieEntityWithTransientAnnotation::getId), null)
                .set(field(MovieEntityWithTransientAnnotation::getCreationDateTime), now)
                .set(field(MovieEntityWithTransientAnnotation::getUpdateDateTime), now)
                .create();

        // Act
        datasetManager.create(expectedMovieDataset);

        // Assert
        expectedMovieDataset.setId(1L);
        expectedMovieDataset.setGenre(null);
        final var actualMovieDataset = MovieEntityWithTransientAnnotation.findById(1L);
        assertThat(actualMovieDataset).isEqualTo(expectedMovieDataset);
    }

    @Test
    void test_movieEntityWithoutTableAnnotation() throws SQLException {

        // Arrange
        final var now = truncateToMicroseconds(LocalDateTime.now());
        final var expectedMovieDataset = Instancio.of(MovieEntityWithoutTableAnnotation.class)
                .set(field(MovieEntityWithoutTableAnnotation::getId), null)
                .set(field(MovieEntityWithoutTableAnnotation::getCreationDateTime), now)
                .set(field(MovieEntityWithoutTableAnnotation::getUpdateDateTime), now)
                .create();

        // Act
        datasetManager.create(expectedMovieDataset);

        // Assert
        expectedMovieDataset.setId(1L);
        final var actualMovieDataset = MovieEntityWithoutTableAnnotation.findById(1L);
        assertThat(actualMovieDataset).isEqualTo(expectedMovieDataset);
    }

    @Test
    void test_movieEntityWithIdGeneratedBySequence() throws SQLException {

        // Arrange
        final var now = truncateToMicroseconds(LocalDateTime.now());
        final long expectedMovieId = 1L;
        final var expectedMovieDataset = Instancio.of(MovieEntityWithIdGeneratedBySequence.class)
                .set(field(MovieEntityWithIdGeneratedBySequence::getId), null)
                .set(field(MovieEntityWithIdGeneratedBySequence::getCreationDateTime), now)
                .set(field(MovieEntityWithIdGeneratedBySequence::getUpdateDateTime), now)
                .create();

        // Act
        datasetManager.create(expectedMovieDataset);

        // Assert
        final var actualMovieDataset = MovieEntityWithIdGeneratedBySequence.findById(expectedMovieId);
        expectedMovieDataset.setId(expectedMovieId);
        assertThat(actualMovieDataset).isEqualTo(expectedMovieDataset);
    }

    @Test
    void test_movieEntityWithIdGeneratedByTable() throws SQLException {

        // Arrange
        final var now = truncateToMicroseconds(LocalDateTime.now());
        final long expectedMovieId = 1L;
        final var expectedMovieDataset = Instancio.of(MovieEntityWithIdGeneratedByTable.class)
                .set(field(MovieEntityWithIdGeneratedByTable::getId), null)
                .set(field(MovieEntityWithIdGeneratedByTable::getCreationDateTime), now)
                .set(field(MovieEntityWithIdGeneratedByTable::getUpdateDateTime), now)
                .create();

        // Act
        datasetManager.create(expectedMovieDataset);

        // Assert
        final var actualMovieDataset = MovieEntityWithIdGeneratedByTable.findById(expectedMovieId);
        expectedMovieDataset.setId(expectedMovieId);
        assertThat(actualMovieDataset).isEqualTo(expectedMovieDataset);
    }

    @Test
    void test_movieEntityWithIdGeneratedAsAuto() throws SQLException {

        // Arrange
        final var now = truncateToMicroseconds(LocalDateTime.now());
        final long expectedMovieId = 1L;
        final var expectedMovieDataset = Instancio.of(MovieEntityWithIdGeneratedAutomatically.class)
                .set(field(MovieEntityWithIdGeneratedAutomatically::getId), null)
                .set(field(MovieEntityWithIdGeneratedAutomatically::getCreationDateTime), now)
                .set(field(MovieEntityWithIdGeneratedAutomatically::getUpdateDateTime), now)
                .create();

        // Act
        datasetManager.create(expectedMovieDataset);

        // Assert
        final var actualMovieDataset = MovieEntityWithIdGeneratedAutomatically.findById(expectedMovieId);
        expectedMovieDataset.setId(expectedMovieId);
        assertThat(actualMovieDataset).isEqualTo(expectedMovieDataset);
    }

    private LocalDateTime truncateToMicroseconds(final LocalDateTime localDateTime) {
        return localDateTime.truncatedTo(ChronoUnit.MICROS);
    }
}