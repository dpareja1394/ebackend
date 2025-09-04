package co.edu.usbcali.cinema.service;

import co.edu.usbcali.cinema.dto.MovieDetailResponseDTO;

import java.util.List;

public interface MovieDetailService {
    List<MovieDetailResponseDTO> findAll();
}
