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

    private final GenreService genreService;

    @GetMapping("/all")
    public List<String> getGenresString() {
        return genreService.getGenresString();
    }

    @GetMapping("/all/object")
    public List<GenreResponseDTO> getGenresResponseDTO() {
        return genreService.getGenresResponseDTO();
    }

    @PostMapping
    public ResponseEntity<GenreResponseDTO> saveGenre(
            @RequestBody GenreRequestDTO genreRequestDTO) throws Exception {
        return new ResponseEntity<>
                (genreService.saveGenre(genreRequestDTO), HttpStatus.CREATED);
    }

}
