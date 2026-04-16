package co.edu.usbcali.cinemapareja.service;

import co.edu.usbcali.cinemapareja.dto.request.CreateScreeningRequest;
import co.edu.usbcali.cinemapareja.dto.response.CreateScreeningResponse;

public interface ScreeningService {
    CreateScreeningResponse createScreening(CreateScreeningRequest createScreeningRequest) throws Exception;
}
