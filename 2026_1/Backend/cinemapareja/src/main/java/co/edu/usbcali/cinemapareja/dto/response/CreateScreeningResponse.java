package co.edu.usbcali.cinemapareja.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateScreeningResponse {
    private Integer id;
    private String movieName;
    private String theaterName;
    private String dateTime;
}
