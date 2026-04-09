package co.edu.usbcali.cinemapareja.mapper;

import co.edu.usbcali.cinemapareja.dto.request.CreateMovieRequest;
import co.edu.usbcali.cinemapareja.dto.response.GetMovieResponse;
import co.edu.usbcali.cinemapareja.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieMapper {
    public static GetMovieResponse entityToGetMovieResponse(Movie movie) {
        //Instanciar nuevo objeto GetMovieResponse
        GetMovieResponse getMovieResponse = GetMovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .build();
        return getMovieResponse;
    }

    public static List<GetMovieResponse> entityToListGetMovieResponse(List<Movie> movies) {
        /*// Instanciar lista de DTO GetMovieResponse vacía inicialmente
        List<GetMovieResponse> getMovieResponseList = new ArrayList<>();

        // Iterar sobre la lista de objetos Movie y agregarlos a la lista de objetos GetMovieResponse
        for(int i = 0; i < movies.size(); i++) {
            // Por cada iteración de la lista, obtener el objeto Movie actual y convertirlo a DTO GetMovieResponse
            Movie movie = movies.get(i);
            GetMovieResponse getMovieResponse = entityToGetMovieResponse(movie);
            // Agregar el objeto DTO GetMovieResponse a la lista de objetos GetMovieResponse
            getMovieResponseList.add(getMovieResponse);
        }

        // Retornar la lista de DTO GetMovieResponse
        return getMovieResponseList;*/
        return movies.stream().map(MovieMapper::entityToGetMovieResponse).toList();
    }

    public static Movie createMovieRequestToEntity(CreateMovieRequest createMovieRequest) {
        /*// Instanciar objeto Movie
        Movie movie = new Movie();

        // Asignar valores a los atributos del objeto Movie
        movie.setTitle(createMovieRequest.getTitle());
        movie.setDescription(createMovieRequest.getDescription());

        // Retornar objeto Movie
        return movie;*/
        return Movie.builder()
                .title(createMovieRequest.getTitle())
                .description(createMovieRequest.getDescription())
                .build();
    }
}
