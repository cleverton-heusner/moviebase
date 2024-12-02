package cleverton.heusner.adapter.output.mapper;

import cleverton.heusner.adapter.output.response.MovieRatingResponse;
import cleverton.heusner.domain.model.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.CDI;

@Mapper(componentModel = CDI)
public interface MovieRatingResponseMapper {

    @Mapping(target = "value", source = "normalizedValue")
    Movie.Rating toModel(final MovieRatingResponse.RatingResponse ratingResponse);
}