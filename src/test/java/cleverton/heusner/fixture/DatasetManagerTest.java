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
        datasetManager.clean(MovieEntityMockWithColumnAnnotation.class);
        datasetManager.clean(MovieEntityMockWithoutColumnAnnotation.class);
    }

    @Test
    void test_movieEntityWithColumnAnnotation() throws SQLException {

        // Arrange
        final var now = truncateToMicroseconds(LocalDateTime.now());
        final var expectedMovieDataset = Instancio.of(MovieEntityMockWithColumnAnnotation.class)
                .set(field(MovieEntityMockWithColumnAnnotation::getId), null)
                .set(field(MovieEntityMockWithColumnAnnotation::getCreationDateTime), now)
                .set(field(MovieEntityMockWithColumnAnnotation::getUpdateDateTime), now)
                .create();

        // Act
        datasetManager.create(expectedMovieDataset);

        // Assert
        expectedMovieDataset.setId(1L);
        final var actualMovieDataset = MovieEntityMockWithColumnAnnotation.findById(1L);
        assertThat(actualMovieDataset).isEqualTo(expectedMovieDataset);
    }

    @Test
    void test_movieEntityWithoutColumnAnnotation() throws SQLException {

        // Arrange
        final var now = truncateToMicroseconds(LocalDateTime.now());
        final var expectedMovieDataset = Instancio.of(MovieEntityMockWithoutColumnAnnotation.class)
                .set(field(MovieEntityMockWithoutColumnAnnotation::getId), null)
                .set(field(MovieEntityMockWithoutColumnAnnotation::getCreationDateTime), now)
                .set(field(MovieEntityMockWithoutColumnAnnotation::getUpdateDateTime), now)
                .create();

        // Act
        datasetManager.create(expectedMovieDataset);

        // Assert
        expectedMovieDataset.setId(1L);
        final var actualMovieDataset = MovieEntityMockWithoutColumnAnnotation.findById(1L);
        assertThat(actualMovieDataset).isEqualTo(expectedMovieDataset);
    }

    @Test
    void test_movieEntityWithTransientAnnotation() throws SQLException {

        // Arrange
        final var now = truncateToMicroseconds(LocalDateTime.now());
        final var expectedMovieDataset = Instancio.of(MovieEntityMockWithTransientAnnotation.class)
                .set(field(MovieEntityMockWithTransientAnnotation::getId), null)
                .set(field(MovieEntityMockWithTransientAnnotation::getCreationDateTime), now)
                .set(field(MovieEntityMockWithTransientAnnotation::getUpdateDateTime), now)
                .create();

        // Act
        datasetManager.create(expectedMovieDataset);

        // Assert
        expectedMovieDataset.setId(1L);
        expectedMovieDataset.setGenre(null);
        final var actualMovieDataset = MovieEntityMockWithTransientAnnotation.findById(1L);
        assertThat(actualMovieDataset).isEqualTo(expectedMovieDataset);
    }

    @Test
    void test_movieEntityWithoutTableAnnotation() throws SQLException {

        // Arrange
        final var now = truncateToMicroseconds(LocalDateTime.now());
        final var expectedMovieDataset = Instancio.of(MovieEntityMockWithoutTableAnnotation.class)
                .set(field(MovieEntityMockWithoutTableAnnotation::getId), null)
                .set(field(MovieEntityMockWithoutTableAnnotation::getCreationDateTime), now)
                .set(field(MovieEntityMockWithoutTableAnnotation::getUpdateDateTime), now)
                .create();

        // Act
        datasetManager.create(expectedMovieDataset);

        // Assert
        expectedMovieDataset.setId(1L);
        final var actualMovieDataset = MovieEntityMockWithoutTableAnnotation.findById(1L);
        assertThat(actualMovieDataset).isEqualTo(expectedMovieDataset);
    }

    @Test
    void test_movieEntityWithIdGeneratedBySequence() throws SQLException {

        // Arrange
        final var now = truncateToMicroseconds(LocalDateTime.now());
        final long expectedMovieId = 1L;
        final var expectedMovieDataset = Instancio.of(MovieEntityMockWithIdGeneratedBySequence.class)
                .set(field(MovieEntityMockWithIdGeneratedBySequence::getId), null)
                .set(field(MovieEntityMockWithIdGeneratedBySequence::getCreationDateTime), now)
                .set(field(MovieEntityMockWithIdGeneratedBySequence::getUpdateDateTime), now)
                .create();

        // Act
        datasetManager.create(expectedMovieDataset);

        // Assert
        final var actualMovieDataset = MovieEntityMockWithIdGeneratedBySequence.findById(expectedMovieId);
        expectedMovieDataset.setId(expectedMovieId);
        assertThat(actualMovieDataset).isEqualTo(expectedMovieDataset);
    }

    @Test
    void test_movieEntityWithIdGeneratedByTable() throws SQLException {

        // Arrange
        final var now = truncateToMicroseconds(LocalDateTime.now());
        final long expectedMovieId = 1L;
        final var expectedMovieDataset = Instancio.of(MovieEntityMockWithIdGeneratedByTable.class)
                .set(field(MovieEntityMockWithIdGeneratedByTable::getId), null)
                .set(field(MovieEntityMockWithIdGeneratedByTable::getCreationDateTime), now)
                .set(field(MovieEntityMockWithIdGeneratedByTable::getUpdateDateTime), now)
                .create();

        // Act
        datasetManager.create(expectedMovieDataset);

        // Assert
        final var actualMovieDataset = MovieEntityMockWithIdGeneratedByTable.findById(expectedMovieId);
        expectedMovieDataset.setId(expectedMovieId);
        assertThat(actualMovieDataset).isEqualTo(expectedMovieDataset);
    }

    private LocalDateTime truncateToMicroseconds(final LocalDateTime localDateTime) {
        return localDateTime.truncatedTo(ChronoUnit.MICROS);
    }
}