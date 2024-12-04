package cleverton.heusner.domain.service;

import cleverton.heusner.domain.model.Movie;
import cleverton.heusner.port.input.service.MovieProvider;
import cleverton.heusner.port.shared.LoggerService;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceImplTest {

    @Mock
    private MovieProvider movieProvider;

    @Mock
    private LoggerService logger;

    @InjectMocks
    private MovieServiceImpl movieService;

    @Test
    void given_movieIsValid_when_movieIsCreated_then_createdMovieIsReturned() {

        // Arrange
        final var movieToCreate = Instancio.of(Movie.class)
                .set(Select.field(Movie::getId), null)
                .set(Select.field(Movie::getReleaseYear), LocalDate.now().getYear())
                .create();

        final var expectedCreatedMovie = new Movie.Builder()
                .id(1L).
                isan(movieToCreate.getIsan())
                .title(movieToCreate.getTitle())
                .build();

        when(movieProvider.findByIsan(movieToCreate.getIsan())).thenReturn(Optional.empty());
        when(movieProvider.create(movieToCreate)).thenReturn(expectedCreatedMovie);
        doNothing().when(logger).info(anyString(), any(Object[].class));

        // Act
        final Movie actualCreatedMovie = movieService.create(movieToCreate);

        // Assert
        assertThat(actualCreatedMovie).isEqualTo(expectedCreatedMovie);

        verify(movieProvider).findByIsan(movieToCreate.getIsan());
        verify(movieProvider).create(movieToCreate);
        verify(logger, times(3)).info(anyString(), any(Object[].class));
        verify(logger, never()).warn(anyString(), any(Object[].class));
    }
}