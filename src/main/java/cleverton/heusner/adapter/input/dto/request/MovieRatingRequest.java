package cleverton.heusner.adapter.input.dto.request;

import cleverton.heusner.adapter.input.validation.Rating;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "MovieRatingRequest", description = "Movie rating request representation.")
public record MovieRatingRequest (
        @Schema(
                required = true,
                example = "9.5",
                description = "Movie's rating."
        )
        @NotNull(message = "{NotNull.movie.rating}")
        @Rating(message = "{Invalid.movie.rating}")
        Double rating
) {}