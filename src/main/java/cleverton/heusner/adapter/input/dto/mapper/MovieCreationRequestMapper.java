package cleverton.heusner.adapter.input.dto.mapper;

import cleverton.heusner.adapter.input.dto.request.MovieCreationRequest;
import cleverton.heusner.domain.model.Movie;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.CDI;

@Mapper(componentModel = CDI)
public interface MovieCreationRequestMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "isan", source = "isan")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "releaseYear", source = "releaseYear")
    Movie toModel(final MovieCreationRequest movieCreationRequest);
}