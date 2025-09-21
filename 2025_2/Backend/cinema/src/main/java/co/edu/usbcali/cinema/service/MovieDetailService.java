package co.edu.usbcali.cinema.service;

import co.edu.usbcali.cinema.dto.MovieDetailRequestDTO;
import co.edu.usbcali.cinema.dto.MovieDetailResponseDTO;

import java.util.List;

public interface MovieDetailService {
    List<MovieDetailResponseDTO> findAll();
    MovieDetailResponseDTO saveMovieDetail(MovieDetailRequestDTO movieDetailRequestDTO) throws Exception;
}
