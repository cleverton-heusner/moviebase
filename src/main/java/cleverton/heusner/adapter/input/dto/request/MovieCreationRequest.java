package cleverton.heusner.adapter.input.dto.request;

import cleverton.heusner.adapter.input.validation.Year;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "MovieCreationRequest", description = "Movie creation request representation.")
public record MovieCreationRequest(
        @Schema(
                required = true,
                example = "The Shawshank Redemption",
                minLength = 1,
                maxLength = 40,
                description = "Movie's name."
        )
        @NotNull(message = "{NotNull.movie.title}")
        @Size(message = "{Invalid.movie.title}", min = 1, max = 40)
        String title,

        @Schema(
                required = true,
                example = "000000002A560000Y000000002",
                minLength = 26,
                maxLength = 26,
                description = "International Standard Audiovisual Number"
        )
        @NotNull(message = "{NotNull.movie.isan}")
        @Size(message = "{Invalid.movie.isan}", min = 26, max = 26)
        String isan,

        @Schema(
                example = "2000",
                minLength = 4,
                maxLength = 4,
                description = "Movie's release year"
        )
        @Year(message = "{Invalid.movie.releaseYear}")
        int releaseYear
) {}