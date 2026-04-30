package co.edu.usbcali.cinemapareja.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class UpdateScreeningRequest {
    private Integer theaterId;
    private Timestamp dateTime;
}
