package cleverton.heusner.adapter.input.resource;

import cleverton.heusner.adapter.input.dto.mapper.*;
import cleverton.heusner.adapter.input.dto.request.MovieCreationRequest;
import cleverton.heusner.adapter.input.dto.request.MovieRatingRequest;
import cleverton.heusner.adapter.input.dto.request.MovieUpdateRequest;
import cleverton.heusner.adapter.input.dto.response.MovieResponse;
import cleverton.heusner.adapter.input.dto.response.MovieWithRatingsResponse;
import cleverton.heusner.domain.model.Movie;
import cleverton.heusner.port.input.service.MovieService;
import cleverton.heusner.port.shared.LoggerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import static jakarta.ws.rs.core.Response.Status.CREATED;

@Tag(name="Movie Resource", description = "Movie REST API")
@Path("movies")
public class MovieResource {

    private final MovieService movieService;
    private final MovieCreationRequestMapper movieCreationRequestMapper;
    private final MovieUpdateRequestMapper movieUpdateRequestMapper;
    private final MovieResponseMapper movieResponseMapper;
    private final MovieWithRatingsResponseMapper movieWithRatingsResponseMapper;
    private final MovieRatingRequestMapper movieRatingRequestMapper;
    private final MovieWithRatingResponseMapper movieWithRatingResponseMapper;
    private final LoggerService logger;

    public MovieResource(final MovieService movieService,
                         final MovieCreationRequestMapper movieCreationRequestMapper,
                         final MovieUpdateRequestMapper movieUpdateRequestMapper,
                         final MovieResponseMapper movieResponseMapper,
                         final MovieWithRatingsResponseMapper movieWithRatingsResponseMapper,
                         final MovieRatingRequestMapper movieRatingRequestMapper,
                         final MovieWithRatingResponseMapper movieWithRatingResponseMapper,
                         final LoggerService logger) {
        this.movieService = movieService;
        this.movieCreationRequestMapper = movieCreationRequestMapper;
        this.movieUpdateRequestMapper = movieUpdateRequestMapper;
        this.movieResponseMapper = movieResponseMapper;
        this.movieWithRatingsResponseMapper = movieWithRatingsResponseMapper;
        this.movieRatingRequestMapper = movieRatingRequestMapper;
        this.movieWithRatingResponseMapper = movieWithRatingResponseMapper;
        this.logger = logger;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Operation(
            operationId = "getMovies",
            summary = "Get Movies.",
            description = "Get all movies inside the list."
    )
    @APIResponse(
            responseCode = "200",
            description = "Movies listed.",
            content = @Content(mediaType = APPLICATION_JSON)
    )
    public Response getMovies() {
        final List<MovieResponse> movies = movieService.list()
                .stream()
                .map(movieResponseMapper::toResponse)
                .toList();
        logger.info("All movies created listed successfully.");
        return Response.ok(movies).build();
    }

    @GET
    @Path("{id}/ratings")
    @Produces(APPLICATION_JSON)
    @Operation(
            operationId = "getRatingsById",
            summary = "Get movie's ratings.",
            description = "Get movie's ratings by id."
    )
    @APIResponse(
            responseCode = "200",
            description = "Movie's ratings found.",
            content = @Content(mediaType = APPLICATION_JSON)
    )
    public Response getRatingsById(
            @Parameter(
                    description = "Movie's id.",
                    required = true
            )
            @Valid
            @NotNull(message = "Id is required.")
            @PathParam("id")
            final Long movieId) {

        final Movie movie = movieService.getRatingsById(movieId);
        final MovieWithRatingsResponse movieWithRatings = movieWithRatingsResponseMapper.toResponse(movie);

        logger.info("Ratings of movie retrieved by ID '%' successfully.", movieId);
        return Response.ok(movieWithRatings).build();
    }

    @PATCH
    @Path("{id}/rating")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @Operation(
            operationId = "rateMovieById",
            summary = "Rate movie by id.",
            description = "Rate movie by id inside the list."
    )
    @APIResponse(
            responseCode = "200",
            description = "Movie rated.",
            content = @Content(mediaType = APPLICATION_JSON)
    )
    public Response rateMovieById(
            @Parameter(
                    description = "Movie's id.",
                    required = true
            )
            @Valid
            @NotNull(message = "Id is required.")
            @PathParam("id")
            final Long movieId,

            @Valid
            @RequestBody(
                    description = "Movie to rate.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = MovieRatingRequest.class)
                    )
            )
            final MovieRatingRequest movieRatingRequest) {

        final Movie movieToRate = movieRatingRequestMapper.toModel(movieRatingRequest);
        final Movie ratedMovie = movieService.rateById(movieId, movieToRate);

        logger.info("Movie rated by ID '%' with value '%' successfully.", movieId, movieRatingRequest.rating());
        return Response.ok(movieWithRatingResponseMapper.toResponse(ratedMovie)).build();
    }

    @GET
    @Produces(TEXT_PLAIN)
    @Path("size")
    @Operation(
            operationId = "countMovies",
            summary = "Count Movies",
            description = "Count all movies inside the list."
    )
    @APIResponse(
            responseCode = "200",
            description = "Movies counted.",
            content = @Content(mediaType = TEXT_PLAIN)
    )
    public Response countMovies() {
        logger.info("All movies counted successfully.");
        return Response.ok(movieService.countMovies()).build();
    }

    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @Operation(
            operationId = "createMovie",
            summary = "Create Movie.",
            description = "Create a new movie inside the list."
    )
    @APIResponse(
            responseCode = "201",
            description = "Movie created.",
            content = @Content(mediaType = APPLICATION_JSON)
    )
    public Response createMovie(
            @RequestBody(
                    description = "Movie to create.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MovieCreationRequest.class))
            )
            @Valid
            final MovieCreationRequest movieCreationRequest) {

        final Movie movieToCreate = movieCreationRequestMapper.toModel(movieCreationRequest);
        final Movie createdMovie = movieService.create(movieToCreate);

        logger.info(
                "Movie with title '%' and ISAN '%' created successfully.",
                createdMovie.getTitle(),
                createdMovie.getIsan()
        );
        return Response.status(CREATED)
                .entity(movieResponseMapper.toResponse(createdMovie))
                .build();
    }

    @PATCH
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @Operation(
            operationId = "updateMovieById",
            summary = "Update movie by id.",
            description = "Update movie by id inside the list."
    )
    @APIResponse(
            responseCode = "200",
            description = "Movie updated.",
            content = @Content(mediaType = APPLICATION_JSON)
    )
    public Response updateMovieById(
            @Parameter(
                    description = "Movie's id.",
                    required = true
            )
            @Valid
            @NotNull(message = "Id is required.")
            @PathParam("id")
            final Long movieId,

            @Valid
            @RequestBody(
                    description = "Movie to update.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = MovieUpdateRequest.class)
                    )
            )
            final MovieUpdateRequest movieUpdateRequest) {

        final Movie movieToUpdate = movieUpdateRequestMapper.toModel(movieUpdateRequest);
        final Movie updatedMovie = movieService.updateById(movieId, movieToUpdate);

        logger.info("Movie updated by ID '%' successfully.", updatedMovie.getId());
        return Response.ok(movieResponseMapper.toResponse(updatedMovie)).build();
    }

    @DELETE
    @Path("{id}")
    @Consumes(TEXT_PLAIN)
    @Operation(
            operationId = "deleteMovieById",
            summary = "Delete movie by id.",
            description = "Delete movie by id inside the list."
    )
    @APIResponse(
            responseCode = "204",
            description = "Movie deleted.",
            content = @Content(mediaType = APPLICATION_JSON)
    )
    public Response deleteMovieById(
            @Parameter(
                    description = "Movie's id.",
                    required = true
            )
            @Valid
            @NotNull(message = "Id is required.")
            @PathParam("id")
            final Long movieId) {

        movieService.deleteById(movieId);
        logger.info("Movie deleted by ID '%' successfully.", movieId);
        return Response.noContent().build();
    }
}