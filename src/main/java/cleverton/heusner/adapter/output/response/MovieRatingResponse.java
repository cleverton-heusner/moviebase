package cleverton.heusner.adapter.output.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.stream.Stream;

import static cleverton.heusner.adapter.output.response.MovieRatingResponse.RatingFormat.*;
import static java.lang.Double.parseDouble;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieRatingResponse (

        @JsonProperty("Ratings")
        List<RatingResponse> ratings,

        @JsonProperty("Metascore")
        String metaScore,

        String imdbRating,

        @JsonProperty("Response")
        String response,

        @JsonProperty("Error")
        String error
) {
    public List<RatingResponse> getRatings() {
        return Stream.concat(getRatingsNoDuplicates(), getRemainingRatings())
                .toList();
    }

    private Stream<RatingResponse> getRatingsNoDuplicates() {
        return ratings.stream()
                .filter(ratingResponse -> ratingResponse.getSource().startsWith("Rotten Tomatoes"))
                .map(rating -> rating.setRatingFormat(PERCENT))
                .map(RatingResponse::normalizeValue);
    }

    private Stream<RatingResponse> getRemainingRatings() {
        return Stream.of(
                new RatingResponse("Metascore", metaScore, SCALE_100),
                new RatingResponse("IMDb", imdbRating, SCALE_10)
        );
    }

    public boolean hasMovieNotFoundError() {
        return "Movie not found!".equals(error);
    }

    @Data
    public static class RatingResponse {

        @JsonProperty("Source")
        private String source;

        @JsonProperty("Value")
        private String value;

        @JsonIgnore
        private Double normalizedValue;

        @JsonIgnore
        private RatingFormat ratingFormat;

        public RatingResponse(final String source, final String value, final RatingFormat ratingFormat) {
            this.source = source;
            this.value = value;
            this.ratingFormat = ratingFormat;

            normalizeValue();
        }

        public RatingResponse setRatingFormat(final RatingFormat ratingFormat) {
            this.ratingFormat = ratingFormat;
            return this;
        }

        private RatingResponse normalizeValue() {
            if (SCALE_100.equals(ratingFormat)) {
                normalizedValue = parseDouble(value) / 10;
            }
            else if (PERCENT.equals(ratingFormat)) {
                normalizedValue = parseDouble(value.split("%")[0]) / 10;
            }
            else if (SCALE_10.equals(ratingFormat)) {
                normalizedValue = parseDouble(value);
            }

            return this;
        }
    }

    public enum RatingFormat {
        SCALE_10, SCALE_100, PERCENT
    }
}