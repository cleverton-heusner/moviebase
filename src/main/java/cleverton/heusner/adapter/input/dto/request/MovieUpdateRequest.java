package cleverton.heusner.adapter.input.dto.request;

import cleverton.heusner.adapter.input.validation.Year;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import static cleverton.heusner.shared.constant.validation.MovieValidation.*;

@Schema(name = "MovieUpdateRequest", description = "Movie update request representation.")
public record MovieUpdateRequest(
        @Schema(
                required = true,
                example = "Charlie and the Chocolate Factory",
                minLength = TITLE_MIN_SIZE,
                maxLength = TITLE_MAX_SIZE,
                description = "Movie's name."
        )
        @NotNull(message = "{NotNull.movie.title}")
        @Size(message = "{Invalid.movie.title}", min = TITLE_MIN_SIZE, max = TITLE_MAX_SIZE)
        String title,

        @Schema(
                example = "2000",
                minLength = YEAR_SIZE,
                maxLength = YEAR_SIZE,
                description = "Movie's release year"
        )
        @Year(message = "{Invalid.movie.releaseYear}")
        int releaseYear
) {}