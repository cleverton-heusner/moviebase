package cleverton.heusner.adapter.input.dto.mapper;

import cleverton.heusner.adapter.input.dto.response.MovieWithRatingResponse;
import cleverton.heusner.domain.model.Movie;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.CDI;

@Mapper(componentModel = CDI)
public interface MovieWithRatingResponseMapper {

    MovieWithRatingResponse toResponse(final Movie movie);
}