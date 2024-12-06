package cleverton.heusner.adapter.input.dto.request;

import cleverton.heusner.adapter.input.validation.Year;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import static cleverton.heusner.shared.constant.validation.MovieValidation.*;

@Schema(name = "MovieCreationRequest", description = "Movie creation request representation.")
public record MovieCreationRequest(
        @Schema(
                required = true,
                example = "The Shawshank Redemption",
                minLength = TITLE_MIN_SIZE,
                maxLength = TITLE_MAX_SIZE,
                description = "Movie's name."
        )
        @NotNull(message = "{NotNull.movie.title}")
        @Size(message = "{Invalid.movie.title}", min = TITLE_MIN_SIZE, max = TITLE_MAX_SIZE)
        String title,

        @Schema(
                required = true,
                example = "000000002A560000Y000000002",
                minLength = ISAN_SIZE,
                maxLength = ISAN_SIZE,
                description = "International Standard Audiovisual Number"
        )
        @NotNull(message = "{NotNull.movie.isan}")
        @Size(message = "{Invalid.movie.isan}", min = ISAN_SIZE, max = ISAN_SIZE)
        String isan,

        @Schema(
                example = "2000",
                minLength = YEAR_SIZE,
                maxLength = YEAR_SIZE,
                description = "Movie's release year"
        )
        @Year(message = "{Invalid.movie.releaseYear}")
        int releaseYear
) {}