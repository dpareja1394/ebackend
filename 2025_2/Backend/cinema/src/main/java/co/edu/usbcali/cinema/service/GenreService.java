package co.edu.usbcali.cinema.service;

import co.edu.usbcali.cinema.dto.GenreRequestDTO;
import co.edu.usbcali.cinema.dto.GenreResponseDTO;

import java.util.List;

public interface GenreService {
    List<GenreResponseDTO> getGenres();
    String findNameGenreById(Integer id);
    GenreResponseDTO findGenreById(Integer id);
    GenreResponseDTO saveGenre(GenreRequestDTO genreRequestDTO) throws Exception;
}
