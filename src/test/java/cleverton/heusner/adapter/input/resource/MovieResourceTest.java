package cleverton.heusner.adapter.input.resource;

import cleverton.heusner.adapter.input.dto.response.MovieResponse;
import cleverton.heusner.adapter.input.dto.response.MovieWithRatingsResponse;
import cleverton.heusner.adapter.input.exception.ErrorResponse;
import cleverton.heusner.adapter.output.entity.MovieEntity;
import cleverton.heusner.domain.model.Movie;
import io.quarkiverse.wiremock.devservice.ConnectWireMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.instancio.Instancio;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.http.HttpStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@QuarkusTest
@TestHTTPEndpoint(MovieResource.class)
@ConnectWireMock
public class MovieResourceTest extends ResourceTest {

    @Test
    @Tag("list_movies")
    void given_existingMovies_when_moviesListed_then_okResponseReturned() throws SQLException {

        // Arrange
        final var now = truncateToMicroseconds(LocalDateTime.now());
        final var movieDataset1 = Instancio.of(MovieEntity.class)
                .set(field(MovieEntity::getId), null)
                .set(field(MovieEntity::getCreationDateTime), now)
                .set(field(MovieEntity::getUpdateDateTime), now)
                .create();
        final var movieDataset2 = Instancio.of(MovieEntity.class)
                .set(field(MovieEntity::getId), null)
                .set(field(MovieEntity::getCreationDateTime), now)
                .set(field(MovieEntity::getUpdateDateTime), now)
                .create();
        insertMoviesDataset(movieDataset1, movieDataset2);

        final var expectedMovieResponse1 = createMovieResponseFromDataset(1L, movieDataset1);
        final var expectedMovieResponse2 = createMovieResponseFromDataset(2L, movieDataset2);
        final List<MovieResponse> expectedResponseMovies = List.of(expectedMovieResponse1, expectedMovieResponse2);

        // Act
        final Response response = given().accept(APPLICATION_JSON)
                .when()
                .get();
        final List<MovieResponse> actualMovies = response.as(new TypeRef<>(){});

        // Assert
        assertThat(response.statusCode()).isEqualTo(SC_OK);
        assertThat(response.contentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(actualMovies).isEqualTo(expectedResponseMovies);
    }

    @Test
    @Tag("list_movies")
    void given_noExistingMovies_when_moviesListed_then_okResponseReturned() {

        // Arrange
        final List<Movie> expectedMovies = Collections.emptyList();

        // Act
        final Response response = given().accept(APPLICATION_JSON)
                .when()
                .get();
        final List<Movie> actualMovies = response.as(new TypeRef<>(){});

        // Assert
        assertThat(response.statusCode()).isEqualTo(SC_OK);
        assertThat(response.contentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(actualMovies).isEqualTo(expectedMovies);
    }

    @Test
    @Tag("get_movie_ratings")
    void given_movieWithMovieBaseRatingAndApiRatings_when_ratingsQueried_then_okResponseReturned() throws SQLException {

        // Arrange
        mockMovieRatingApiWithMovieRatingResponse();

        final long movieId = 1;

        final var now = truncateToMicroseconds(LocalDateTime.now());
        final var ratingEntity = Instancio.of(MovieEntity.RatingEntity.class)
                .set(field(MovieEntity.RatingEntity::getSource), "moviebase")
                .set(field(MovieEntity.RatingEntity::getValue), 1.1)
                .create();
        final var movieDataset = Instancio.of(MovieEntity.class)
                .set(field(MovieEntity::getId), null)
                .set(field(MovieEntity::getTitle), "The%20Shawshank%20Redemption")
                .set(field(MovieEntity::getRatingEntity), ratingEntity)
                .set(field(MovieEntity::getCreationDateTime), now)
                .set(field(MovieEntity::getUpdateDateTime), now)
                .create();
        insertMoviesDataset(movieDataset);

        final var rating1 = Instancio.of(Movie.Rating.class)
                .set(field(Movie.Rating::getSource), "Rotten Tomatoes")
                .set(field(Movie.Rating::getValue), 8.1)
                .create();
        final var rating2 = Instancio.of(Movie.Rating.class)
                .set(field(Movie.Rating::getSource), "Metascore")
                .set(field(Movie.Rating::getValue), 8.1)
                .create();
        final var rating3 = Instancio.of(Movie.Rating.class)
                .set(field(Movie.Rating::getSource), "IMDb")
                .set(field(Movie.Rating::getValue), 9.1)
                .create();
        final var rating4 = Instancio.of(Movie.Rating.class)
                .set(field(Movie.Rating::getSource), movieDataset.getRatingEntity().getSource())
                .set(field(Movie.Rating::getValue), movieDataset.getRatingEntity().getValue())
                .create();
        final List<Movie.Rating> ratings = List.of(rating1, rating2, rating3, rating4);
        final var expectedMovieWithRatingsResponse = Instancio.of(MovieWithRatingsResponse.class)
                .set(field(MovieWithRatingsResponse::id), 1L)
                .set(field(MovieWithRatingsResponse::title), movieDataset.getTitle())
                .set(field(MovieWithRatingsResponse::ratings), ratings)
                .set(field(MovieWithRatingsResponse::ratingsAverage), 6.6)
                .create();

        // Act
        final Response response = given().accept(APPLICATION_JSON)
                .when()
                .pathParam("id", movieId)
                .get("{id}/ratings");
        final var actualMovieWithRatingsResponse = response.as(MovieWithRatingsResponse.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(SC_OK);
        assertThat(response.contentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(actualMovieWithRatingsResponse).isEqualTo(expectedMovieWithRatingsResponse);
    }

    @Test
    @Tag("get_movie_ratings")
    void given_movieNoMovieBaseRatingAndWithApiRatings_when_ratingsQueried_then_okResponseReturned() throws SQLException {

        // Arrange
        mockMovieRatingApiWithMovieRatingResponse();

        final long movieId = 1;

        final var now = truncateToMicroseconds(LocalDateTime.now());
        final var movieDataset = Instancio.of(MovieEntity.class)
                .set(field(MovieEntity::getId), null)
                .set(field(MovieEntity::getTitle), "The%20Shawshank%20Redemption")
                .set(field(MovieEntity::getRatingEntity), null)
                .set(field(MovieEntity::getCreationDateTime), now)
                .set(field(MovieEntity::getUpdateDateTime), now)
                .create();
        insertMoviesDataset(movieDataset);

        final var rating1 = Instancio.of(Movie.Rating.class)
                .set(field(Movie.Rating::getSource), "Rotten Tomatoes")
                .set(field(Movie.Rating::getValue), 8.1)
                .create();
        final var rating2 = Instancio.of(Movie.Rating.class)
                .set(field(Movie.Rating::getSource), "Metascore")
                .set(field(Movie.Rating::getValue), 8.1)
                .create();
        final var rating3 = Instancio.of(Movie.Rating.class)
                .set(field(Movie.Rating::getSource), "IMDb")
                .set(field(Movie.Rating::getValue), 9.1)
                .create();
        final List<Movie.Rating> ratings = List.of(rating1, rating2, rating3);
        final var expectedMovieWithRatingsResponse = Instancio.of(MovieWithRatingsResponse.class)
                .set(field(MovieWithRatingsResponse::id), 1L)
                .set(field(MovieWithRatingsResponse::title), movieDataset.getTitle())
                .set(field(MovieWithRatingsResponse::ratings), ratings)
                .set(field(MovieWithRatingsResponse::ratingsAverage), 8.4)
                .create();

        // Act
        final Response response = given().accept(APPLICATION_JSON)
                .when()
                .pathParam("id", movieId)
                .get("{id}/ratings");
        final var actualMovieWithRatingsResponse = response.as(MovieWithRatingsResponse.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(SC_OK);
        assertThat(response.contentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(actualMovieWithRatingsResponse).isEqualTo(expectedMovieWithRatingsResponse);
    }

    @Test
    @Tag("get_movie_ratings")
    void given_movieWithMovieBaseRatingAndNoApiRatings_when_ratingsQueried_then_okResponseReturned() throws SQLException {

        // Arrange
        mockMovieRatingApiWithNotFoundErrorResponse();

        final long movieId = 1;

        final var now = truncateToMicroseconds(LocalDateTime.now());
        final var ratingEntity = Instancio.of(MovieEntity.RatingEntity.class)
                .set(field(MovieEntity.RatingEntity::getSource), "moviebase")
                .set(field(MovieEntity.RatingEntity::getValue), 1.1)
                .create();
        final var movieDataset = Instancio.of(MovieEntity.class)
                .set(field(MovieEntity::getId), null)
                .set(field(MovieEntity::getTitle), "nonExistentMovie")
                .set(field(MovieEntity::getRatingEntity), ratingEntity)
                .set(field(MovieEntity::getCreationDateTime), now)
                .set(field(MovieEntity::getUpdateDateTime), now)
                .create();
        insertMoviesDataset(movieDataset);

        final var rating = Instancio.of(Movie.Rating.class)
                .set(field(Movie.Rating::getSource), movieDataset.getRatingEntity().getSource())
                .set(field(Movie.Rating::getValue), movieDataset.getRatingEntity().getValue())
                .create();
        final var expectedMovieWithRatingsResponse = Instancio.of(MovieWithRatingsResponse.class)
                .set(field(MovieWithRatingsResponse::id), 1L)
                .set(field(MovieWithRatingsResponse::title), movieDataset.getTitle())
                .set(field(MovieWithRatingsResponse::ratings), List.of(rating))
                .set(field(MovieWithRatingsResponse::ratingsAverage), 1.1)
                .create();

        // Act
        final Response response = given().accept(APPLICATION_JSON)
                .when()
                .pathParam("id", movieId)
                .get("{id}/ratings");
        final var actualMovieWithRatingsResponse = response.as(MovieWithRatingsResponse.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(SC_OK);
        assertThat(response.contentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(actualMovieWithRatingsResponse).isEqualTo(expectedMovieWithRatingsResponse);
    }

    @Test
    @Tag("create_movie")
    void given_movieIsValid_when_movieIsCreated_then_createdResponseReturned() {

        // Arrange
        final var validMovieToCreate = createValidMovieCreationRequest();
        final var expectedCreatedMovie = Instancio.of(Movie.class)
                .set(field(Movie::getId), 1L)
                .set(field(Movie::getIsan), validMovieToCreate.isan())
                .set(field(Movie::getTitle), validMovieToCreate.title())
                .set(field(Movie::getReleaseYear), validMovieToCreate.releaseYear())
                .set(field(Movie::getRating), null)
                .set(field(Movie::getRatings), null)
                .set(field(Movie::getRatingsAverage), 0.0)
                .set(field(Movie::getUpdateDateTime), null)
                .create();

        // Act
        final Response response = given().contentType(APPLICATION_JSON)
                .body(validMovieToCreate)
                .when()
                .post();
        final var actualCreatedMovie = response.as(Movie.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(SC_CREATED);
        assertThat(response.contentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(actualCreatedMovie).usingRecursiveComparison()
                .ignoringFields("creationDateTime")
                .isEqualTo(expectedCreatedMovie);
        assertThat(actualCreatedMovie.getCreationDateTime()).isNotNull();
    }

    @Test
    @Tag("create_movie")
    void given_movieWithUndersizedIsan_when_movieIsCreated_then_badRequestResponseReturned() {

        // Arrange
        final String undersizedIsan = "000000002A560000Y00000000";
        final var movieWithInvalidIsan = createMovieCreationRequest(undersizedIsan);
        final var expectedErrorResponse = createBadRequestErrorResponse(
                "createMovie.movieCreationRequest.isan: ISAN deve ter '26' caracteres."
        );

        // Act
        final Response response = given().contentType(APPLICATION_JSON)
                .body(movieWithInvalidIsan)
                .when()
                .post();
        final var actualErrorResponse = response.as(ErrorResponse.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(SC_BAD_REQUEST);
        assertThat(response.contentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(actualErrorResponse).usingRecursiveComparison()
                .ignoringFields("dateTime")
                .isEqualTo(expectedErrorResponse);
        assertThat(actualErrorResponse.getDateTime()).isNotNull();
    }

    @Test
    @Tag("create_movie")
    void given_movieWithOverSizedIsan_when_movieIsCreated_then_badRequestResponseReturned() {

        // Arrange
        final String overSizedIsan = "000000002A560000Y0000000022";
        final var movieWithInvalidIsan = createMovieCreationRequest(overSizedIsan);
        final var expectedErrorResponse = createBadRequestErrorResponse(
                "createMovie.movieCreationRequest.isan: ISAN deve ter '26' caracteres."
        );

        // Act
        final Response response = given().contentType(APPLICATION_JSON)
                .body(movieWithInvalidIsan)
                .when()
                .post();
        final var actualErrorResponse = response.as(ErrorResponse.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(SC_BAD_REQUEST);
        assertThat(response.contentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(actualErrorResponse).usingRecursiveComparison()
                .ignoringFields("dateTime")
                .isEqualTo(expectedErrorResponse);
        assertThat(actualErrorResponse.getDateTime()).isNotNull();
    }

    @Test
    @Tag("create_movie")
    void given_movieWithReleaseYearHavingLessThanFourDigits_when_movieIsCreated_then_badRequestResponseReturned() {

        // Arrange
        final int releaseYearWithLessThanFourDigits = 200;
        final var movieWithReleaseYearHavingLessThanFourDigits = createMovieCreationRequest(releaseYearWithLessThanFourDigits);
        final var expectedErrorResponse = createBadRequestErrorResponse(
                "createMovie.movieCreationRequest.releaseYear: Ano de lançamento inválido."
        );

        // Act
        final Response response = given().contentType(APPLICATION_JSON)
                .body(movieWithReleaseYearHavingLessThanFourDigits)
                .when()
                .post();
        final var actualErrorResponse = response.as(ErrorResponse.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(SC_BAD_REQUEST);
        assertThat(response.contentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(actualErrorResponse).usingRecursiveComparison()
                .ignoringFields("dateTime")
                .isEqualTo(expectedErrorResponse);
        assertThat(actualErrorResponse.getDateTime()).isNotNull();
    }

    @Test
    @Tag("create_movie")
    void given_movieWithReleaseYearHavingMoreThanFourDigits_when_movieIsCreated_then_badRequestResponseReturned() {

        // Arrange
        final int releaseYearWithMoreThanFourDigits = 20000;
        final var movieWithReleaseYearHavingMoreThanFourDigits = createMovieCreationRequest(releaseYearWithMoreThanFourDigits);
        final var expectedErrorResponse = createBadRequestErrorResponse(
                "createMovie.movieCreationRequest.releaseYear: Ano de lançamento inválido."
        );

        // Act
        final Response response = given().contentType(APPLICATION_JSON)
                .body(movieWithReleaseYearHavingMoreThanFourDigits)
                .when()
                .post();
        final var actualErrorResponse = response.as(ErrorResponse.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(SC_BAD_REQUEST);
        assertThat(response.contentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(actualErrorResponse).usingRecursiveComparison()
                .ignoringFields("dateTime")
                .isEqualTo(expectedErrorResponse);
        assertThat(actualErrorResponse.getDateTime()).isNotNull();
    }

    @Test
    @Tag("create_movie")
    void given_movieWithReleaseYearBeforeFirstMovieYear_when_movieIsCreated_then_badRequestResponseReturned() {

        // Arrange
        final int releaseYearBeforeFirstMovieYear = 1887;
        final var movieWithReleaseYearBeforeFirstMovieYear = createMovieCreationRequest(releaseYearBeforeFirstMovieYear);
        final var expectedErrorResponse = createBusinessErrorResponse("Ano de lançamento não pode ser antes de 1888.");

        // Act
        final Response response = given().contentType(APPLICATION_JSON)
                .body(movieWithReleaseYearBeforeFirstMovieYear)
                .when()
                .post();
        final var actualErrorResponse = response.as(ErrorResponse.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(422);
        assertThat(response.contentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(actualErrorResponse).usingRecursiveComparison()
                .ignoringFields("dateTime")
                .isEqualTo(expectedErrorResponse);
        assertThat(actualErrorResponse.getDateTime()).isNotNull();
    }

    @Test
    @Tag("create_movie")
    void given_movieWithReleaseYearInTheFuture_when_movieIsCreated_then_badRequestResponseReturned() {

        // Arrange
        final int releaseYearInTheFuture = LocalDate.now().plusYears(1).getYear();
        final var movieWithReleaseYearInTheFuture = createMovieCreationRequest(releaseYearInTheFuture);
        final var expectedErrorResponse = createBusinessErrorResponse("Ano de lançamento não pode ser no futuro.");

        // Act
        final Response response = given().contentType(APPLICATION_JSON)
                .body(movieWithReleaseYearInTheFuture)
                .when()
                .post();
        final var actualErrorResponse = response.as(ErrorResponse.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(422);
        assertThat(response.contentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(actualErrorResponse).usingRecursiveComparison()
                .ignoringFields("dateTime")
                .isEqualTo(expectedErrorResponse);
        assertThat(actualErrorResponse.getDateTime()).isNotNull();
    }
}