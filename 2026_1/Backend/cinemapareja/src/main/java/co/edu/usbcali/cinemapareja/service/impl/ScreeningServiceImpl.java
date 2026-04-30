package co.edu.usbcali.cinemapareja.service.impl;

import co.edu.usbcali.cinemapareja.dto.request.CreateScreeningRequest;
import co.edu.usbcali.cinemapareja.dto.request.UpdateScreeningRequest;
import co.edu.usbcali.cinemapareja.dto.response.CreateScreeningResponse;
import co.edu.usbcali.cinemapareja.dto.response.UpdateScreeningResponse;
import co.edu.usbcali.cinemapareja.mapper.ScreeningMapper;
import co.edu.usbcali.cinemapareja.model.Movie;
import co.edu.usbcali.cinemapareja.model.Screening;
import co.edu.usbcali.cinemapareja.model.Theater;
import co.edu.usbcali.cinemapareja.repository.MovieRepository;
import co.edu.usbcali.cinemapareja.repository.ScreeningRepository;
import co.edu.usbcali.cinemapareja.repository.TheaterRespository;
import co.edu.usbcali.cinemapareja.service.ScreeningService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ScreeningServiceImpl implements ScreeningService {
    private final ScreeningRepository screeningRepository;
    private final TheaterRespository theaterRespository;
    private final MovieRepository movieRepository;


    @Override
    public CreateScreeningResponse createScreening(CreateScreeningRequest createScreeningRequest) throws Exception {
        try {
            // Validar cada campo
            // Validar que el objeto no sea nulo
            if(Objects.isNull(createScreeningRequest)) {
                throw new Exception("El objeto CreateScreeningRequest no puede ser nulo.");
            }

            // Validar que el campo movieId venga con valor
            if(Objects.isNull(createScreeningRequest.getMovieId()) || createScreeningRequest.getMovieId() <= 0) {
                throw new Exception("El campo movieId no puede ser nulo.");
            }

            // Validar que el campo theaterId venga con valor
            if(Objects.isNull(createScreeningRequest.getTheaterId()) || createScreeningRequest.getTheaterId() <= 0) {
                throw new Exception("El campo theaterId no puede ser nulo.");
            }

            // Validar el campo de DateTime venga con un valor
            if(Objects.isNull(createScreeningRequest.getDateTime())) {
                throw new Exception("La fecha no puede ser nula.");
            }

            // Validar que Movie exista en base de datos
            Movie movie = movieRepository.findById(createScreeningRequest.getMovieId())
                    .orElseThrow(() -> new Exception("No se ha encontrado la movie con id "+createScreeningRequest.getMovieId()));

            // Validar que Theater exista en base de datos
            Theater theater = theaterRespository.findById(createScreeningRequest.getTheaterId())
                    .orElseThrow(() -> new Exception("No se ha encontrado el Theater con id "+createScreeningRequest.getTheaterId()));


            // Convertir a Entity Screening
            Screening screening = Screening.builder()
                    .movie(movie)
                    .theater(theater)
                    .dateTime(createScreeningRequest.getDateTime())
                    .build();

            // Persistir en base de datos
            screening = screeningRepository.save(screening);
            // Mapear la entidad a DTO y retornar
            return ScreeningMapper.entityToCreateScreeningResponse(screening);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public UpdateScreeningResponse updateScreening(
            Long id, UpdateScreeningRequest updateScreeningRequest) throws Exception {
        try {
            // Validar cada campo
            // Validar que el objeto no sea nulo
            if(Objects.isNull(updateScreeningRequest)) {
                throw new Exception("El objeto UpdateScreeningRequest no puede ser nulo.");
            }
            // Validar el id que llega en el método
            if(Objects.isNull(id) || id <= 0) {
                throw new Exception("El id no puede ser nulo, tampoco puede ser menor o igual a 0.");
            }

            // Validar que el campo theaterId venga con valor
            if(Objects.isNull(updateScreeningRequest.getTheaterId()) ||
                    updateScreeningRequest.getTheaterId() <= 0) {
                throw new Exception("El campo theaterId no puede ser nulo.");
            }

            // Validar el campo de DateTime venga con un valor
            if(Objects.isNull(updateScreeningRequest.getDateTime())) {
                throw new Exception("La fecha no puede ser nula.");
            }

            // Validar que el Screening exista en base de datos
            Screening screening = screeningRepository.findById(id)
                    .orElseThrow(() -> new Exception("No se ha encontrado un Screening con id "+id));

            // Validar que Theater exista en base de datos
            Theater theater = theaterRespository.findById(updateScreeningRequest.getTheaterId())
                    .orElseThrow(
                            () -> new Exception("No se ha encontrado el Theater con id "+updateScreeningRequest.getTheaterId()));

            // Actualizar los datos del Screening
            screening.setTheater(theater);
            screening.setDateTime(updateScreeningRequest.getDateTime());

            screening = screeningRepository.save(screening);
            return ScreeningMapper.entityToUpdateScreeningResponse(screening);
        } catch (Exception e) {
            throw e;
        }
    }
}
