package co.edu.usbcali.parqueaderos.controller;

import co.edu.usbcali.parqueaderos.dto.ParqueaderoResponse;
import co.edu.usbcali.parqueaderos.service.ParqueaderoService;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** API interna para otros servicios; el gateway no la enruta. */
@RestController
@RequestMapping("/internal/v1/parqueaderos")
public class InternalParqueaderoController {

    private final ParqueaderoService service;

    public InternalParqueaderoController(ParqueaderoService service) {
        this.service = service;
    }

    @GetMapping("/{parqueaderoId}")
    public ParqueaderoResponse obtener(@PathVariable UUID parqueaderoId) {
        return service.obtener(parqueaderoId);
    }
}
