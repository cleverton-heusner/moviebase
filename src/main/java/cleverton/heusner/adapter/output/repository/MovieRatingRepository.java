package cleverton.heusner.adapter.output.repository;

import cleverton.heusner.adapter.output.response.MovieRatingResponse;
import io.quarkus.rest.client.reactive.ClientQueryParam;
import io.quarkus.rest.client.reactive.ClientQueryParams;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestResponse;

@RegisterRestClient(configKey = "movie-api")
@ClientQueryParams({
        @ClientQueryParam(name="apikey", value = "${movie-api.apikey}"),
        @ClientQueryParam(name="type", value = "${movie-api.type}")
})
public interface MovieRatingRepository {

    @GET
    @Produces("application/json")
    RestResponse<MovieRatingResponse> getByTitle(@QueryParam("t") final String title);
}