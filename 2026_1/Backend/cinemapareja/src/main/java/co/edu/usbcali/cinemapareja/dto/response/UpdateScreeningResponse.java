package co.edu.usbcali.cinemapareja.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateScreeningResponse {
    private Integer id;
    private String movieName;
    private String theaterName;
    private String dateTime;
}
