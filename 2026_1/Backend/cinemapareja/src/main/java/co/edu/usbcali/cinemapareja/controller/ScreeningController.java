package co.edu.usbcali.cinemapareja.controller;

import co.edu.usbcali.cinemapareja.dto.request.CreateScreeningRequest;
import co.edu.usbcali.cinemapareja.dto.request.UpdateScreeningRequest;
import co.edu.usbcali.cinemapareja.dto.response.CreateScreeningResponse;
import co.edu.usbcali.cinemapareja.dto.response.UpdateScreeningResponse;
import co.edu.usbcali.cinemapareja.service.ScreeningService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/proyecciones")
public class ScreeningController {
    private final ScreeningService screeningService;

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @PostMapping("/create")
    public ResponseEntity<CreateScreeningResponse> createScreening(
            @RequestBody CreateScreeningRequest createScreeningRequest) throws Exception {
        CreateScreeningResponse screeningCreated = screeningService.createScreening(createScreeningRequest);
        return new ResponseEntity<>(
                screeningCreated,
                HttpStatus.CREATED
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UpdateScreeningResponse> updateScreening(
            @PathVariable("id") Long id,
            @RequestBody UpdateScreeningRequest updateScreeningRequest) throws Exception {
        UpdateScreeningResponse screeningUpdated = screeningService.updateScreening(id, updateScreeningRequest);
        return new ResponseEntity<>(
                screeningUpdated,
                HttpStatus.OK
        );
    }

}
