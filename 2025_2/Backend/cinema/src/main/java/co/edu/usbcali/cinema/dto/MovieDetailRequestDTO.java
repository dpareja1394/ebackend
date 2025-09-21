package co.edu.usbcali.cinema.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDetailRequestDTO {

    private Integer durationMinutes;
    private LocalDate releaseDate;
    private String imageUrl;
    private Integer movieId;
    private Integer ratingId;
    private Integer genreId;

}
