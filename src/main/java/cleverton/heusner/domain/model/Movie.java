package cleverton.heusner.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static java.math.RoundingMode.FLOOR;

public class Movie {

    private static final int FIRST_MOVIE_YEAR = 1888;

    private Long id;
    private String isan;
    private String title;
    private int releaseYear;
    private Rating rating;
    private List<Rating> ratings;
    private double ratingsAverage;
    private LocalDateTime creationDateTime;
    private LocalDateTime updateDateTime;

    public Movie(final Long id,
                 final String isan,
                 final String title,
                 final int releaseYear,
                 final Rating rating,
                 final List<Rating> ratings,
                 final double ratingsAverage,
                 final LocalDateTime creationDateTime,
                 final LocalDateTime updateDateTime) {
        this.id = id;
        this.isan = isan;
        this.title = title;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.ratings = ratings;
        this.ratingsAverage = ratingsAverage;
        this.creationDateTime = creationDateTime;
        this.updateDateTime = updateDateTime;
    }

    public Movie createdAtNow() {
        creationDateTime = LocalDateTime.now();
        return this;
    }

    public Movie updatedAtNow() {
        updateDateTime = LocalDateTime.now();
        return this;
    }

    public boolean isReleaseYearInFuture() {
        return releaseYear > LocalDate.now().getYear();
    }

    public boolean isReleaseYearBeforeFirstMovieYear() {
        return releaseYear < FIRST_MOVIE_YEAR;
    }

    public Movie withRatingsAverage() {
        if (rating != null) {
            ratings.add(rating);
        }

        ratingsAverage = BigDecimal.valueOf(
                ratings.stream()
                        .mapToDouble(Rating::getValue)
                        .average()
                        .orElse(0.0)
                )
                .setScale(1, FLOOR)
                .doubleValue();
        return this;
    }

    public Movie withRatings(final List<Rating> ratings) {
        this.ratings = ratings;
        return this;
    }

    public Movie withRating(final Rating rating) {
        this.rating = rating;
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsan() {
        return isan;
    }

    public void setIsan(String isan) {
        this.isan = isan;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public double getRatingsAverage() {
        return ratingsAverage;
    }

    public void setRatingsAverage(double ratingsAverage) {
        this.ratingsAverage = ratingsAverage;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", isan='" + isan + '\'' +
                ", title='" + title + '\'' +
                ", releaseYear=" + releaseYear +
                ", rating=" + rating +
                ", ratings=" + ratings +
                ", ratingsAverage=" + ratingsAverage +
                ", creationDateTime=" + creationDateTime +
                ", updateDateTime=" + updateDateTime +
                '}';
    }

    public static class Builder {

        private Long id;
        private String isan;
        private String title;
        private int releaseYear;
        private Rating rating;
        private List<Rating> ratings;
        private double ratingsAverage;
        private LocalDateTime creationDateTime;
        private LocalDateTime updateDateTime;

        public Builder() {}

        public Builder id(final Long id) {
            this.id = id;
            return this;
        }

        public Builder isan(final String isan) {
            this.isan = isan;
            return this;
        }

        public Builder title(final String title) {
            this.title = title;
            return this;
        }

        public Builder releaseYear(final int releaseYear) {
            this.releaseYear = releaseYear;
            return this;
        }

        public Builder rating(final Rating rating) {
            this.rating = rating;
            return this;
        }

        public Builder ratings(final List<Rating> ratings) {
            this.ratings = ratings;
            return this;
        }

        public Builder ratingsAverage(final double ratingsAverage) {
            this.ratingsAverage = ratingsAverage;
            return this;
        }

        public Builder creationDateTime(final LocalDateTime creationDateTime) {
            this.creationDateTime = creationDateTime;
            return this;
        }

        public Builder updateDateTime(final LocalDateTime updateDateTime) {
            this.updateDateTime = updateDateTime;
            return this;
        }

        public Movie build() {
            return new Movie(
                    id,
                    isan,
                    title,
                    releaseYear,
                    rating,
                    ratings,
                    ratingsAverage,
                    creationDateTime,
                    updateDateTime
            );
        }
    }

    public static class Rating {

        private String source;
        private Double value;

        public Rating() {}

        public Rating(String source, Double value) {
            this.source = source;
            this.value = value;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Rating rating = (Rating) o;
            return Objects.equals(source, rating.source) && Objects.equals(value, rating.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(source, value);
        }

        @Override
        public String toString() {
            return "Rating{" +
                    "source='" + source + '\'' +
                    ", value=" + value +
                    '}';
        }
    }
}