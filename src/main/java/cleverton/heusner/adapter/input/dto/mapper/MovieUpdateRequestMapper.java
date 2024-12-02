package cleverton.heusner.adapter.input.dto.mapper;

import cleverton.heusner.adapter.input.dto.request.MovieUpdateRequest;
import cleverton.heusner.domain.model.Movie;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.CDI;

@Mapper(componentModel = CDI)
public interface MovieUpdateRequestMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "releaseYear", source = "releaseYear")
    Movie toModel(final MovieUpdateRequest movieUpdateRequest);
}