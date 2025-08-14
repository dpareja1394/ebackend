package co.edu.usbcali.cinema.mapper;

import co.edu.usbcali.cinema.domain.Genre;
import co.edu.usbcali.cinema.dto.GenreResponseDTO;

public class GenreMapper {

    public static GenreResponseDTO domainToGenreResponseDTO(Genre domain) {
        GenreResponseDTO response = GenreResponseDTO.builder()
                .name(domain.getName())
                .build();
        return response;
    }

}
