package co.edu.usbcali.cinemapareja.controller;

import co.edu.usbcali.cinemapareja.dto.request.CreateMovieRequest;
import co.edu.usbcali.cinemapareja.dto.response.GetMovieResponse;
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

    private final MovieService movieService;

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/all")
    public List<GetMovieResponse> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetMovieResponse> getMovieById(@PathVariable Integer id) {
        GetMovieResponse movieResponse = movieService.getMovieById(id);
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
