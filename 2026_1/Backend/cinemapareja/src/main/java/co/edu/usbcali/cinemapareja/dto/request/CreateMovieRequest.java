package co.edu.usbcali.cinemapareja.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateMovieRequest {
    private String title;
    private String description;
}
