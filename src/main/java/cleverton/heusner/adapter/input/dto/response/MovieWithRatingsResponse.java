package cleverton.heusner.adapter.input.dto.response;

import cleverton.heusner.domain.model.Movie;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(name = "MovieWithRatingsResponse", description = "Movie with ratings response representation.")
public record MovieWithRatingsResponse (
        @Schema(description = "Movie's id.")
        Long id,

        @Schema(description = "Movie's name")
        String title,

        @Schema(description = "Movie's ratings")
        List<Movie.Rating> ratings,

        @Schema(description = "Movie's ratings average")
        double ratingsAverage
) {}