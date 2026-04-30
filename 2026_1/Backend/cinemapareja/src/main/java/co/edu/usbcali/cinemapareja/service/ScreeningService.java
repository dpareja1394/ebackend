package co.edu.usbcali.cinemapareja.service;

import co.edu.usbcali.cinemapareja.dto.request.CreateScreeningRequest;
import co.edu.usbcali.cinemapareja.dto.request.UpdateScreeningRequest;
import co.edu.usbcali.cinemapareja.dto.response.CreateScreeningResponse;
import co.edu.usbcali.cinemapareja.dto.response.UpdateScreeningResponse;

public interface ScreeningService {
    CreateScreeningResponse createScreening(CreateScreeningRequest createScreeningRequest) throws Exception;
    UpdateScreeningResponse updateScreening(Long id, UpdateScreeningRequest updateScreeningRequest) throws Exception;
}
