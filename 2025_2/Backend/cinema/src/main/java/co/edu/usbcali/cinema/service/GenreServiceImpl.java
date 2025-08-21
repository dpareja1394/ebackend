package co.edu.usbcali.cinema.service;

import co.edu.usbcali.cinema.domain.Genre;
import co.edu.usbcali.cinema.dto.GenreResponseDTO;
import co.edu.usbcali.cinema.mapper.GenreMapper;
import co.edu.usbcali.cinema.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService{

    private final GenreRepository genreRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GenreResponseDTO> getGenres() {
        // Consulto todos en Base de Datos
        List<Genre> genres = genreRepository.findAll();

        // Mapeo a Lista de DTOs
        List<GenreResponseDTO> responseDTOs = GenreMapper
                .domainListToGenreResponseDTOList(genres);

        // Retorno la información de la lista de DTOs
        return responseDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public String findNameGenreById(Integer id) {
        Optional<Genre> genreOptional = genreRepository.findById(id);
        if (genreOptional.isPresent()) {
            return genreOptional.get().getName();
        }
        return "Not found";
    }

    @Override
    @Transactional(readOnly = true)
    public GenreResponseDTO findGenreById(Integer id) {
        Optional<Genre> genreOptional = genreRepository.findById(id);
        if (genreOptional.isPresent()) {
            GenreResponseDTO response = GenreMapper
                    .domainToGenreResponseDTO(genreOptional.get());
            return response;
        }
        return null;
    }
}
