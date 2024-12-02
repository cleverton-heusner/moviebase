package cleverton.heusner.adapter.input.dto.mapper;

import cleverton.heusner.adapter.input.dto.response.MovieWithRatingsResponse;
import cleverton.heusner.domain.model.Movie;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.CDI;

@Mapper(componentModel = CDI)
public interface MovieWithRatingsResponseMapper {

    MovieWithRatingsResponse toResponse(final Movie movie);
}