package co.edu.usbcali.cinema.controller;

import co.edu.usbcali.cinema.dto.MovieDetailRequestDTO;
import co.edu.usbcali.cinema.dto.MovieDetailResponseDTO;
import co.edu.usbcali.cinema.service.MovieDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/movie-detail")
@RestController
@RequiredArgsConstructor
public class MovieDetailController {
    private final MovieDetailService movieDetailService;

    @GetMapping("/all")
    List<MovieDetailResponseDTO> getAll() {
        return movieDetailService.findAll();
    }

    @PostMapping
    ResponseEntity<MovieDetailResponseDTO> saveMovieDetail(@RequestBody MovieDetailRequestDTO movieDetailRequestDTO)
            throws Exception{
        return new ResponseEntity<>(
                movieDetailService.saveMovieDetail(movieDetailRequestDTO),
                HttpStatus.OK);
    }
}
