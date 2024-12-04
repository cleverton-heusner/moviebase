package cleverton.heusner.adapter.output.provider;

import cleverton.heusner.adapter.output.mapper.MovieRatingResponseMapper;
import cleverton.heusner.adapter.output.repository.MovieRatingRepository;
import cleverton.heusner.adapter.output.response.MovieRatingResponse;
import cleverton.heusner.domain.model.Movie;
import cleverton.heusner.port.output.MovieRatingProvider;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.Dependent;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toCollection;
import static org.jboss.resteasy.reactive.RestResponse.Status.OK;

@Dependent
public class MovieRatingProviderImpl implements MovieRatingProvider {

    private final MovieRatingRepository movieRatingRepository;
    private final MovieRatingResponseMapper movieRatingResponseMapper;

    public MovieRatingProviderImpl(
            @RestClient final MovieRatingRepository movieRatingRepository,
            final MovieRatingResponseMapper movieRatingResponseMapper) {
        this.movieRatingRepository = movieRatingRepository;
        this.movieRatingResponseMapper = movieRatingResponseMapper;
    }

    @CacheResult(cacheName = "movieRatingCache")
    @Override
    public List<Movie.Rating> getByTitle(final String title) {
        final RestResponse<MovieRatingResponse> movieRatingResponse = movieRatingRepository.getByTitle(title);
        if (movieRatingResponse.getStatus() == OK.getStatusCode()) {
            return movieRatingResponse.getEntity().hasMovieNotFoundError() ?
                    new ArrayList<>() :
                    getRatingsFromResponse(movieRatingResponse);
        }

        return new ArrayList<>();
    }

    private ArrayList<Movie.Rating> getRatingsFromResponse(final RestResponse<MovieRatingResponse> movieRatingResponse) {
        return movieRatingResponse.getEntity()
                .getRatings()
                .stream()
                .map(movieRatingResponseMapper::toModel)
                .collect(toCollection(ArrayList::new));
    }
}