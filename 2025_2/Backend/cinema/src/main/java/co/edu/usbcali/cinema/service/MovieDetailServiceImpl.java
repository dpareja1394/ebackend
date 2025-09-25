package co.edu.usbcali.cinema.service;

import co.edu.usbcali.cinema.domain.Genre;
import co.edu.usbcali.cinema.domain.Movie;
import co.edu.usbcali.cinema.domain.MovieDetail;
import co.edu.usbcali.cinema.domain.Rating;
import co.edu.usbcali.cinema.dto.MovieDetailRequestDTO;
import co.edu.usbcali.cinema.dto.MovieDetailResponseDTO;
import co.edu.usbcali.cinema.mapper.MovieDetailMapper;
import co.edu.usbcali.cinema.repository.GenreRepository;
import co.edu.usbcali.cinema.repository.MovieDetailRepository;
import co.edu.usbcali.cinema.repository.MovieRepository;
import co.edu.usbcali.cinema.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieDetailServiceImpl implements MovieDetailService {

    private final MovieDetailRepository movieDetailRepository;
    private final MovieRepository movieRepository;
    private final RatingRepository ratingRepository;
    private final GenreRepository genreRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MovieDetailResponseDTO> findAll() {
        List<MovieDetail> movieDetails = movieDetailRepository.findAll();
        List<MovieDetailResponseDTO> responseDTOS = MovieDetailMapper.domainToResponseDTO(movieDetails);
        return responseDTOS;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public MovieDetailResponseDTO saveMovieDetail(MovieDetailRequestDTO movieDetailRequestDTO) throws Exception {
        // 1. Validar campos
        if (movieDetailRequestDTO == null ) {
            throw new Exception("El detalle no puede ser nulo");
        }

        if (movieDetailRequestDTO.getDurationMinutes() == null || movieDetailRequestDTO.getDurationMinutes() <= 0) {
            throw new Exception("La duración en minutos no puede ser nula ni menor o igual a cero (0)");
        }

        if (movieDetailRequestDTO.getReleaseDate() == null ) {
            throw new Exception("La duración en minutos no puede ser nula ");
        }

        // Las otras validaciones

        // Validar si existe la Movie por Id, en caso de existir la ponemos al MOvie Detail, en caso de no existir lanzamos excepción
        Movie movie = movieRepository
                .findById(movieDetailRequestDTO.getMovieId())
                .orElseThrow(() -> new Exception("No existe la Movie por id "+movieDetailRequestDTO.getMovieId()));

        Rating rating = ratingRepository
                .findById(movieDetailRequestDTO.getRatingId())
                .orElseThrow(() -> new Exception("No existe el Rating por id "+movieDetailRequestDTO.getRatingId()));

        Genre genre = genreRepository
                .findById(movieDetailRequestDTO.getGenreId())
                .orElseThrow(() -> new Exception("No existe el Genre por id "+movieDetailRequestDTO.getGenreId()));

        MovieDetail movieDetail = MovieDetailMapper.requestDTOToEntity(movieDetailRequestDTO);
        movieDetail.setRating(rating);
        movieDetail.setGenre(genre);
        movieDetail.setMovie(movie);

        movieDetail = movieDetailRepository.save(movieDetail);

        return MovieDetailMapper.domainToResponseDTO(movieDetail);
    }

    @Override
    public MovieDetailResponseDTO updateMovieDetail(Integer id, MovieDetailRequestDTO movieDetailRequestDTO) throws Exception {
        return null;
    }
}
