package co.edu.usbcali.cinema.service;

import co.edu.usbcali.cinema.dto.GenreRequestDTO;
import co.edu.usbcali.cinema.dto.GenreResponseDTO;

import java.util.List;

public interface GenreService {
    List<String> getGenresString();
    List<GenreResponseDTO> getGenresResponseDTO();
    GenreResponseDTO saveGenre(GenreRequestDTO genreRequestDTO) throws Exception;
}
