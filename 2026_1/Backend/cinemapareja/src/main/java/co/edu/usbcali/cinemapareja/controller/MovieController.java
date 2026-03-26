package co.edu.usbcali.cinemapareja.controller;

import co.edu.usbcali.cinemapareja.dto.response.GetMovieResponse;
import co.edu.usbcali.cinemapareja.mapper.MovieMapper;
import co.edu.usbcali.cinemapareja.model.Movie;
import co.edu.usbcali.cinemapareja.repository.MovieRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/movies")
public class MovieController {

    private final MovieRepository movieRepository;

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/all")
    public List<GetMovieResponse> getAllMovies() {
        // Declarar nueva lista de moviesResponse
        List<GetMovieResponse> moviesResponse;

        // Ir al Repository y obtener todas las películas
        List<Movie> movies = movieRepository.findAll();

        // Convertir la lista de películas a lista de moviesResponse
        moviesResponse = MovieMapper.entityToListGetMovieResponse(movies);

        // Retornar la lista de moviesResponse
        return moviesResponse;
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetMovieResponse> getMovieById(@PathVariable Integer id) {
        // Buscar Entidad Movie por ID
        Movie movie = movieRepository.getReferenceById(id);

        // Convertir a DTO Response
        GetMovieResponse movieResponse = MovieMapper.entityToGetMovieResponse(movie);

        // Retornar DTO Response
        return new ResponseEntity<>(
                movieResponse,
                HttpStatus.OK
        );
    }
}
