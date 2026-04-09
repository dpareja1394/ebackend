package co.edu.usbcali.cinemapareja.service;

import co.edu.usbcali.cinemapareja.dto.request.CreateMovieRequest;
import co.edu.usbcali.cinemapareja.dto.response.GetMovieResponse;

public interface MovieService {
    GetMovieResponse createMovie(CreateMovieRequest createMovieRequest) throws Exception;
}
