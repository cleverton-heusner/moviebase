package cleverton.heusner.adapter.input.dto.mapper;

import cleverton.heusner.adapter.input.dto.request.MovieRatingRequest;
import cleverton.heusner.domain.model.Movie;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.CDI;

@Mapper(componentModel = CDI)
public interface MovieRatingRequestMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "rating.value", source = "rating")
    Movie toModel(final MovieRatingRequest movieRatingRequest);
}