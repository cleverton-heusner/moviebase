package cleverton.heusner.fixture;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "movie_entity_without_column_annotation")
public class MovieEntityMockWithoutColumnAnnotation extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    private String title;
    private String isan;
    private Integer releaseYear;

    @Embedded
    private RatingEntity ratingEntity;

    private LocalDateTime creationDateTime;
    private LocalDateTime updateDateTime;

    @Embeddable
    @Data
    public static class RatingEntity {
        private String source;
        private Double value;
    }
}