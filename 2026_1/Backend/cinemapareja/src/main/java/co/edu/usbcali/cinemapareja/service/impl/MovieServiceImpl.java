package co.edu.usbcali.cinemapareja.service.impl;

import co.edu.usbcali.cinemapareja.dto.request.CreateMovieRequest;
import co.edu.usbcali.cinemapareja.dto.response.GetMovieResponse;
import co.edu.usbcali.cinemapareja.mapper.MovieMapper;
import co.edu.usbcali.cinemapareja.model.Movie;
import co.edu.usbcali.cinemapareja.repository.MovieRepository;
import co.edu.usbcali.cinemapareja.service.MovieService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MovieServiceImpl implements MovieService{

    // Inyección de dependencias (Repository)
    private final MovieRepository movieRepository;

    @Override
    public GetMovieResponse createMovie(CreateMovieRequest createMovieRequest) throws Exception{
        try {
            // Validar el objeto CreateMovieRequest y todos sus atributos
            if (createMovieRequest == null) {
                throw new Exception("El objeto CreateMovieRequest no puede ser nulo");
            }
            // Validaciones respecto al atributo title
            if (createMovieRequest.getTitle() == null || createMovieRequest.getTitle().isBlank()) {
                throw new Exception("El título es requerido");
            }
            if (createMovieRequest.getTitle().length() > 255) {
                throw new Exception("Título solo soporta hasta 255 caracteres");
            }

            // Convertir desde el Request hacia la Entidad usando el Mapper
            Movie movie = MovieMapper.createMovieRequestToEntity(createMovieRequest);
            // Guardar la Movie (Entidad) en base de datos usando el Repository
            movie = movieRepository.save(movie);

            // Mapear la Entidad a DTO Response
            GetMovieResponse getMovieResponse = MovieMapper.entityToGetMovieResponse(movie);
            // Retornar el DTO Response
            return getMovieResponse;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<GetMovieResponse> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        List<GetMovieResponse> getMovieResponseList = MovieMapper.entityToListGetMovieResponse(movies);
        return getMovieResponseList;
    }

    @Override
    public GetMovieResponse getMovieById(Integer id) {
        Movie movie = movieRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
        GetMovieResponse getMovieResponse = MovieMapper.entityToGetMovieResponse(movie);
        return getMovieResponse;
    }
}
