package cleverton.heusner.adapter.input.dto.response;

import cleverton.heusner.domain.model.Movie;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "MovieWithRatingResponse", description = "Movie with rating response representation.")
public record MovieWithRatingResponse (
        @Schema(description = "Movie's id.")
        Long id,

        @Schema(description = "Movie's name")
        String title,

        @Schema(description = "Movie's rating")
        Movie.Rating rating
) {}