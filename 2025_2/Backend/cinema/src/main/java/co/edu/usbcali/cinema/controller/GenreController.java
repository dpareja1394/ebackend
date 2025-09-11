package co.edu.usbcali.cinema.controller;

import co.edu.usbcali.cinema.dto.GenreRequestDTO;
import co.edu.usbcali.cinema.dto.GenreResponseDTO;
import co.edu.usbcali.cinema.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/genre")
@RestController
@RequiredArgsConstructor
public class GenreController {
    // Inyección de dependencias
    private final GenreService genreService;

    @GetMapping("/ping")
    String ping() {
        return "pong";
    }

    // Método para obtener todos los géneros
    @GetMapping("/all")
    List<GenreResponseDTO> getAll() {
        return genreService.getGenres();
    }

    // Método para obtener el nombre del Género por Id
    @GetMapping("/byId/{id}")
    String findNameById(@PathVariable Integer id) {
        return genreService.findNameGenreById(id);
    }

    // Método para obtener el Género por Id
    @GetMapping("/id/{id}")
    ResponseEntity<GenreResponseDTO> findById(@PathVariable Integer id) {
        GenreResponseDTO response = genreService.findGenreById(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    ResponseEntity<GenreResponseDTO> saveGenre(@RequestBody GenreRequestDTO genreRequestDTO) throws Exception {
        GenreResponseDTO responseDTO = genreService.saveGenre(genreRequestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }


}
