package co.edu.usbcali.cinema.controller;

import co.edu.usbcali.cinema.domain.Genre;
import co.edu.usbcali.cinema.dto.GenreResponseDTO;
import co.edu.usbcali.cinema.mapper.GenreMapper;
import co.edu.usbcali.cinema.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/genre")
@RestController
@RequiredArgsConstructor
public class GenreController {
    // Inyección de dependencias
    private final GenreRepository genreRepository;

    @GetMapping("/ping")
    String ping() {
        return "pong";
    }

    // Método para obtener todos los géneros
    @GetMapping("/all")
    List<Genre> getAll() {
        return genreRepository.findAll();
    }

    // Método para obtener el nombre del Género por Id
    @GetMapping("/byId/{id}")
    String findNameById(@PathVariable Integer id) {
        Genre genre = genreRepository.getReferenceById(id);
        if (genre != null) {
            return genre.getName();
        }
        return "Not found";
    }

    // Método para obtener el Género por Id
    @GetMapping("/id/{id}")
    ResponseEntity<GenreResponseDTO> findById(@PathVariable Integer id) {
        Genre genre = genreRepository.getReferenceById(id);
        if (genre != null) {
            GenreResponseDTO response = GenreMapper
                    .domainToGenreResponseDTO(genre);
            return ResponseEntity.ok(response);
        }
        return null;
    }


}
