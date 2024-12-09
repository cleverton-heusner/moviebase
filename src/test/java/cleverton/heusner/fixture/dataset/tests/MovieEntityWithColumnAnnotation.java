package cleverton.heusner.fixture.dataset.tests;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

import static cleverton.heusner.shared.constant.validation.MovieValidation.ISAN_SIZE;
import static cleverton.heusner.shared.constant.validation.MovieValidation.TITLE_MAX_SIZE;
import static jakarta.persistence.GenerationType.IDENTITY;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "movie_entity_with_column_annotation")
public class MovieEntityWithColumnAnnotation extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    public Long id;

    @Column(name = "title", nullable = false, length = TITLE_MAX_SIZE)
    private String title;

    @Column(name = "isan", nullable = false, length = ISAN_SIZE, unique = true)
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