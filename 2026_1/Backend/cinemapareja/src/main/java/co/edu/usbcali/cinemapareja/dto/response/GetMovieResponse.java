package co.edu.usbcali.cinemapareja.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetMovieResponse {
    // Esta clase lo ideal es que sea inmutable
    private Integer id;
    private String title;
}
