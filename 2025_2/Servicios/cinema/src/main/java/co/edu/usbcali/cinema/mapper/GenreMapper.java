package co.edu.usbcali.cinema.mapper;

import co.edu.usbcali.cinema.domain.Genre;
import co.edu.usbcali.cinema.dto.GenreRequestDTO;
import co.edu.usbcali.cinema.dto.GenreResponseDTO;

import java.util.List;

public class GenreMapper {

    public static GenreResponseDTO entityToDto(Genre genre) {
        return GenreResponseDTO.builder()
                .id(genre.getId())
                .name(genre.getName())
                .build();
    }

    public static List<GenreResponseDTO> entityToDtoList(List<Genre> genres) {
        return genres.stream().map(GenreMapper::entityToDto).toList();
    }

    public static Genre requestDtoToEntity(GenreRequestDTO genreRequestDTO) {
        return Genre.builder()
                .name(genreRequestDTO.getName())
                .build();
    }

}
