package co.edu.usbcali.cinema.service;

import co.edu.usbcali.cinema.domain.Genre;
import co.edu.usbcali.cinema.dto.GenreRequestDTO;
import co.edu.usbcali.cinema.dto.GenreResponseDTO;
import co.edu.usbcali.cinema.mapper.GenreMapper;
import co.edu.usbcali.cinema.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService{

    private final GenreRepository genreRepository;

    @Override
    @Transactional(readOnly = true)
    public List<String> getGenresString() {
        List<Genre> genres = genreRepository.findAll();
        List<String> genresString = new ArrayList<>();
        for (Genre genre : genres) {
            genresString.add(genre.getName());
        }
        return genresString;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GenreResponseDTO> getGenresResponseDTO() {
        List<Genre> genres = genreRepository.findAll();
        List<GenreResponseDTO> responseDTOS = GenreMapper.entityToDtoList(genres);
        return responseDTOS;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public GenreResponseDTO saveGenre(GenreRequestDTO genreRequestDTO) throws Exception {
        // 1. Validar el objeto genreRequestDTO
        // Validar que el objeto no sea nulo
        if (genreRequestDTO == null) {
            throw new Exception("GenreRequestDTO is null");
        }

        // 2. Validar los campos de el objeto
        if (genreRequestDTO.getName() == null || genreRequestDTO.getName().isBlank()) {
            throw new Exception("GenreRequestDTO name is null");
        }

        // Convertir el RequestDTO hacia Entidad
        Genre genre = GenreMapper.requestDtoToEntity(genreRequestDTO);
        genre = genreRepository.save(genre);

        return GenreMapper.entityToDto(genre);
    }
}
