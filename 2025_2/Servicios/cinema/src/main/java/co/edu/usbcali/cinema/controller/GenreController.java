package co.edu.usbcali.cinema.controller;

import co.edu.usbcali.cinema.domain.Genre;
import co.edu.usbcali.cinema.dto.GenreResponseDTO;
import co.edu.usbcali.cinema.mapper.GenreMapper;
import co.edu.usbcali.cinema.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/genre")
@RestController
@RequiredArgsConstructor
public class GenreController {

    private final GenreRepository genreRepository;

    @GetMapping("/all")
    public List<String> getGenresString() {
        List<Genre> genres = genreRepository.findAll();
        List<String> genresString = new ArrayList<>();
        for (Genre genre : genres) {
            genresString.add(genre.getName());
        }
        return genresString;
    }

    @GetMapping("/all/object")
    public List<GenreResponseDTO> getGenresResponseDTO() {
        List<Genre> genres = genreRepository.findAll();
        List<GenreResponseDTO> responseDTOS = GenreMapper.entityToDtoList(genres);
        return responseDTOS;
    }

}
