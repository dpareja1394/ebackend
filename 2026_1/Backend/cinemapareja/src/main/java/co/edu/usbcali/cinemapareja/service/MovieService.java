package co.edu.usbcali.cinemapareja.service;

import co.edu.usbcali.cinemapareja.dto.request.CreateMovieRequest;
import co.edu.usbcali.cinemapareja.dto.response.GetMovieResponse;

import java.util.List;

public interface MovieService {
    GetMovieResponse createMovie(CreateMovieRequest createMovieRequest) throws Exception;
    List<GetMovieResponse> getAllMovies();
    GetMovieResponse getMovieById(Integer id);
}
