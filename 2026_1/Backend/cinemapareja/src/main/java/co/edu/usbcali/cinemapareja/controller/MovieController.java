package co.edu.usbcali.cinemapareja.controller;

import co.edu.usbcali.cinemapareja.dto.request.CreateMovieRequest;
import co.edu.usbcali.cinemapareja.dto.response.GetMovieResponse;
import co.edu.usbcali.cinemapareja.mapper.MovieMapper;
import co.edu.usbcali.cinemapareja.model.Movie;
import co.edu.usbcali.cinemapareja.repository.MovieRepository;
import co.edu.usbcali.cinemapareja.service.MovieService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/movies")
public class MovieController {

    // Inyecciones de dependencias
    private final MovieRepository movieRepository;
    private final MovieService movieService;

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

    @PostMapping("/create")
    public ResponseEntity<GetMovieResponse> createMovie(@RequestBody CreateMovieRequest createMovieRequest) throws Exception {
        GetMovieResponse movieCreated = movieService.createMovie(createMovieRequest);
        return new ResponseEntity<>(
                movieCreated,
                HttpStatus.CREATED
        );
    }

}
