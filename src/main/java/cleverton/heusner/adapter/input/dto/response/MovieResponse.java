package cleverton.heusner.adapter.input.dto.response;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "MovieResponse", description = "Movie response representation.")
public record MovieResponse(
        @Schema(description = "Movie's id.")
        Long id,

        @Schema(description = "International Standard Audiovisual Number")
        String isan,

        @Schema(description = "Movie's name")
        String title,

        @Schema(description = "Movie's release year")
        int releaseYear,

        @Schema(description = "Movie's creation date")
        LocalDateTime creationDateTime,

        @Schema(description = "Movie's update date")
        LocalDateTime updateDateTime) {}