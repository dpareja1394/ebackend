package co.edu.usbcali.cinema.controller;

import co.edu.usbcali.cinema.dto.MovieDetailResponseDTO;
import co.edu.usbcali.cinema.service.MovieDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
