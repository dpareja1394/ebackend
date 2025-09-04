package co.edu.usbcali.cinema.mapper;

import co.edu.usbcali.cinema.domain.Genre;
import co.edu.usbcali.cinema.dto.GenreRequestDTO;
import co.edu.usbcali.cinema.dto.GenreResponseDTO;

import java.util.ArrayList;
import java.util.List;

public class GenreMapper {

    public static GenreResponseDTO domainToGenreResponseDTO(Genre genre) {
        GenreResponseDTO response = GenreResponseDTO.builder()
                .id(genre.getId())
                .name(genre.getName())
                .build();
        return response;
    }

    public static List<GenreResponseDTO> domainListToGenreResponseDTOList(List<Genre> genres) {
        return genres.stream().map(GenreMapper::domainToGenreResponseDTO).toList();
    }

    public static Genre genreRequestToDomain(GenreRequestDTO genreRequestDTO) {
        return Genre.builder()
                .name(genreRequestDTO.getName())
                .build();
    }
}
