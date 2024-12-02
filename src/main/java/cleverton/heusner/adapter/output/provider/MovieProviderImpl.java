package cleverton.heusner.adapter.output.provider;

import cleverton.heusner.adapter.output.entity.MovieEntity;
import cleverton.heusner.adapter.output.mapper.MovieEntityMapper;
import cleverton.heusner.domain.model.Movie;
import cleverton.heusner.port.input.service.MovieProvider;
import cleverton.heusner.port.output.MovieRepository;
import jakarta.enterprise.context.Dependent;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Dependent
public class MovieProviderImpl implements MovieProvider {

    private final MovieRepository movieRepository;
    private final MovieEntityMapper movieEntityMapper;

    public MovieProviderImpl(final MovieRepository movieRepository,
                             final MovieEntityMapper movieEntityMapper) {
        this.movieRepository = movieRepository;
        this.movieEntityMapper = movieEntityMapper;
    }

    @Transactional
    @Override
    public Movie create(final Movie movie) {
        final MovieEntity movieEntity = movieEntityMapper.toEntity(movie);
        movieRepository.persist(movieEntity);
        return movieEntityMapper.toModel(movieEntity);
    }

    @Override
    public Long countMovies() {
        return movieRepository.count();
    }

    @Override
    public List<Movie> list() {
        try (final Stream<MovieEntity> movies = movieRepository.streamAll()) {
            return movies.map(movieEntityMapper::toModel)
                    .toList();
        }
    }

    @Override
    public Optional<Movie> findById(final Long movieId) {
        return movieRepository.findByIdOptional(movieId)
                .map(movieEntityMapper::toModel);
    }

    @Override
    public Optional<Movie> findByIsan(final String isan) {
        return movieRepository.findByIsanOptional(isan)
                .map(movieEntityMapper::toModel);
    }

    @Transactional
    @Override
    public Movie updateById(final Long movieId, final Movie movieToUpdate) {
        movieRepository.updateById(movieId, movieToUpdate);
        return movieEntityMapper.toModel(movieRepository.findById(movieId));
    }

    @Transactional
    @Override
    public Movie rateById(final Long movieId, final Movie movieToRate) {
        movieRepository.rateById(movieId, movieToRate);
        return movieEntityMapper.toModel(movieRepository.findById(movieId));
    }

    @Transactional
    @Override
    public void deleteById(final Long movieId) {
        movieRepository.deleteById(movieId);
    }
}