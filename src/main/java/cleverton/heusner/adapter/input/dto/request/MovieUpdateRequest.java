package cleverton.heusner.adapter.input.dto.request;

import cleverton.heusner.adapter.input.validation.Year;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "MovieUpdateRequest", description = "Movie update request representation.")
public record MovieUpdateRequest(
        @Schema(
                required = true,
                example = "Charlie and the Chocolate Factory",
                minLength = 1,
                maxLength = 40,
                description = "Movie's name."
        )
        @NotNull(message = "{NotNull.movie.title}")
        @Size(message = "{Invalid.movie.title}", min = 1, max = 40)
        String title,

        @Schema(
                example = "2000",
                minLength = 4,
                maxLength = 4,
                description = "Movie's release year"
        )
        @Year(message = "{Invalid.movie.releaseYear}")
        int releaseYear
) {}