package co.edu.usbcali.cinema.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GenreResponseDTO {
    private Integer id;
    private String name;
}
