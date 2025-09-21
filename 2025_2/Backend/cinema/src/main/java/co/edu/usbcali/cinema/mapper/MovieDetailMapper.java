package co.edu.usbcali.cinema.mapper;

import co.edu.usbcali.cinema.domain.MovieDetail;
import co.edu.usbcali.cinema.dto.MovieDetailRequestDTO;
import co.edu.usbcali.cinema.dto.MovieDetailResponseDTO;

import java.util.List;

public class MovieDetailMapper {

    public static MovieDetailResponseDTO domainToResponseDTO(MovieDetail movieDetail) {
        MovieDetailResponseDTO responseDTO = MovieDetailResponseDTO.builder()
                .id(movieDetail.getId())
                .durationMinutes(movieDetail.getDurationMinutes())
                .releaseDate(movieDetail.getReleaseDate())
                .imageUrl(movieDetail.getImageUrl())
                .status(movieDetail.getStatus())
                .movieTitle((movieDetail.getMovie() == null) ? null
                        : movieDetail.getMovie().getTitle())
                .ratingCode((movieDetail.getRating() == null) ? null
                        : movieDetail.getRating().getCode())
                .genreName((movieDetail.getGenre() == null) ? null
                        : movieDetail.getGenre().getName())
                .build();
        return responseDTO;
    }

    public static List<MovieDetailResponseDTO> domainToResponseDTO(List<MovieDetail> movieDetails) {
        return movieDetails.stream().map(MovieDetailMapper::domainToResponseDTO).toList();
    }

    public static MovieDetail requestDTOToEntity(MovieDetailRequestDTO movieDetailRequestDTO) {
        return MovieDetail.builder()
                .durationMinutes(movieDetailRequestDTO.getDurationMinutes())
                .releaseDate(movieDetailRequestDTO.getReleaseDate())
                .imageUrl(movieDetailRequestDTO.getImageUrl())
                .status(true)
                .build();
    }


}
