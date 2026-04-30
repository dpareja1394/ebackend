package co.edu.usbcali.cinemapareja.mapper;

import co.edu.usbcali.cinemapareja.dto.response.CreateScreeningResponse;
import co.edu.usbcali.cinemapareja.dto.response.UpdateScreeningResponse;
import co.edu.usbcali.cinemapareja.model.Screening;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ScreeningMapper {
    public static CreateScreeningResponse entityToCreateScreeningResponse(Screening screening) {
        return CreateScreeningResponse.builder()
                .id(screening.getId())
                .movieName(Objects.nonNull(screening.getMovie()) ? screening.getMovie().getTitle() : null)
                .theaterName(Objects.nonNull(screening.getTheater()) ? screening.getTheater().getName() : null)
                .dateTime(screening.getDateTime().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")))
                .build();
    }

    public static UpdateScreeningResponse entityToUpdateScreeningResponse(Screening screening) {
        return CreateScreeningResponse.builder()
                .id(screening.getId())
                .movieName(Objects.nonNull(screening.getMovie()) ? screening.getMovie().getTitle() : null)
                .theaterName(Objects.nonNull(screening.getTheater()) ? screening.getTheater().getName() : null)
                .dateTime(screening.getDateTime().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")))
                .build();
    }
}
