package cleverton.heusner.fixture;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

import static cleverton.heusner.shared.constant.validation.MovieValidation.ISAN_SIZE;
import static cleverton.heusner.shared.constant.validation.MovieValidation.TITLE_MAX_SIZE;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "movie_entity_with_id_generated_by_sequence")
public class MovieEntityMockWithIdGeneratedBySequence extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_movie")
    @SequenceGenerator(name = "seq_movie", allocationSize = 1)
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