package cleverton.heusner.adapter.input.resource;

import cleverton.heusner.adapter.input.dto.request.MovieCreationRequest;
import cleverton.heusner.adapter.input.dto.response.MovieResponse;
import cleverton.heusner.adapter.input.exception.ErrorResponse;
import cleverton.heusner.adapter.output.entity.MovieEntity;
import cleverton.heusner.fixture.dataset.manager.DatasetManager;
import com.github.tomakehurst.wiremock.client.WireMock;
import jakarta.inject.Inject;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;

public class ResourceTest {

    protected static final String VALID_ISAN = "000000002A560000Y000000002";

    protected WireMock wiremock;

    @Inject
    private DatasetManager datasetManager;

    @AfterEach
    void tearDown() {
        datasetManager.clean(MovieEntity.class);
    }

    protected void insertMoviesDataset(final MovieEntity...movies) {
        datasetManager.create(movies);
    }

    protected LocalDateTime truncateToMicroseconds(final LocalDateTime localDateTime) {
        return localDateTime.truncatedTo(ChronoUnit.MICROS);
    }

    protected MovieCreationRequest createMovieCreationRequest(final int year) {
        return Instancio.of(MovieCreationRequest.class)
                .set(Select.field(MovieCreationRequest::isan), VALID_ISAN)
                .set(Select.field(MovieCreationRequest::releaseYear), year)
                .create();
    }

    protected MovieCreationRequest createMovieCreationRequest(final String isan) {
        return Instancio.of(MovieCreationRequest.class)
                .set(Select.field(MovieCreationRequest::isan), isan)
                .set(Select.field(MovieCreationRequest::releaseYear), 2000)
                .create();
    }

    protected MovieCreationRequest createValidMovieCreationRequest() {
        return Instancio.of(MovieCreationRequest.class)
                .set(Select.field(MovieCreationRequest::isan), VALID_ISAN)
                .set(Select.field(MovieCreationRequest::releaseYear), 2000)
                .create();
    }

    protected MovieResponse createMovieResponseFromDataset(final long id, final MovieEntity movieEntity) {
        return Instancio.of(MovieResponse.class)
                .set(Select.field(MovieResponse::id), id)
                .set(Select.field(MovieResponse::isan), movieEntity.getIsan())
                .set(Select.field(MovieResponse::title), movieEntity.getTitle())
                .set(Select.field(MovieResponse::releaseYear), movieEntity.getReleaseYear())
                .set(Select.field(MovieResponse::creationDateTime), movieEntity.getCreationDateTime())
                .set(Select.field(MovieResponse::updateDateTime), movieEntity.getUpdateDateTime())
                .create();
    }

    protected ErrorResponse createBadRequestErrorResponse(final String message) {
        return Instancio.of(ErrorResponse.class)
                .set(Select.field(ErrorResponse::getTitle), "Bad Request")
                .set(Select.field(ErrorResponse::getDetail), message)
                .set(Select.field(ErrorResponse::getStatus), SC_BAD_REQUEST)
                .create();
    }

    protected ErrorResponse createBusinessErrorResponse(final String message) {
        return Instancio.of(ErrorResponse.class)
                .set(Select.field(ErrorResponse::getTitle), "Business Exception")
                .set(Select.field(ErrorResponse::getDetail), message)
                .set(Select.field(ErrorResponse::getStatus), 422)
                .create();
    }

    protected void mockMovieRatingApiWithMovieRatingResponse() {
        final String mockedMovieRatingResponse = """
                {
                    "Ratings": [
                        {"Source": "Internet Movie Database", "Value": "9.1/10"},
                        {"Source": "Rotten Tomatoes", "Value": "81%"},
                        {"Source": "Metacritic", "Value": "81/100"}
                    ],
                    "Metascore": "81",
                    "imdbRating": "9.1"
                }
                """;

        wiremock.register(get(anyUrl())
                .withQueryParam("t", equalTo("The%20Shawshank%20Redemption"))
                .withQueryParam("apikey", equalTo("apiKeyMock"))
                .withQueryParam("type", equalTo("movie"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockedMovieRatingResponse)));
    }

    protected void mockMovieRatingApiWithNotFoundErrorResponse() {
        final String mockedMovieRatingResponse = """
                {
                    "Response": "False",
                    "Error": "Movie not found!"
                }
                """;

        wiremock.register(get(anyUrl())
                .withQueryParam("t", equalTo("nonExistentMovie"))
                .withQueryParam("apikey", equalTo("apiKeyMock"))
                .withQueryParam("type", equalTo("movie"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockedMovieRatingResponse)));
    }
}