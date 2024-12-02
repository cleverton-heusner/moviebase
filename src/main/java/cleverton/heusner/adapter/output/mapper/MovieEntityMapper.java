package cleverton.heusner.adapter.output.mapper;

import cleverton.heusner.adapter.output.entity.MovieEntity;
import cleverton.heusner.domain.model.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.CDI;

@Mapper(componentModel = CDI)
public interface MovieEntityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ratingEntity", source = "rating")
    MovieEntity toEntity(final Movie movie);

    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "ratingsAverage", ignore = true)

    @Mapping(target = "rating", source = "ratingEntity")
    Movie toModel(final MovieEntity movieEntity);
}