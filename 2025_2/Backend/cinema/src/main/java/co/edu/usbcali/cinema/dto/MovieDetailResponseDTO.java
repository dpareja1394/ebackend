package co.edu.usbcali.cinema.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class MovieDetailResponseDTO {
    private Integer id;
    private Integer durationMinutes;
    private LocalDate releaseDate;
    private String imageUrl;
    private Boolean status;
    private String movieTitle;
    private String ratingCode;
    private String genreName;
}
