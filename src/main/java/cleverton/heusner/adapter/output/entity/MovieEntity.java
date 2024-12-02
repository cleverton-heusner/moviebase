package cleverton.heusner.adapter.output.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "movie")
public class MovieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "title", nullable = false, length = 40)
    private String title;

    @Column(name = "isan", nullable = false, length = 26, unique = true)
    private String isan;

    @Column(name = "release_year")
    private Integer releaseYear;

    @Embedded
    private RatingEntity ratingEntity;

    @Column(name = "creation_date_time")
    private LocalDateTime creationDateTime;

    @Column(name = "update_date_time")
    private LocalDateTime updateDateTime;

    @Embeddable
    @Data
    public static class RatingEntity {

        @Column(name = "source")
        private String source;

        @Column(name = "val")
        private Double value;
    }
}