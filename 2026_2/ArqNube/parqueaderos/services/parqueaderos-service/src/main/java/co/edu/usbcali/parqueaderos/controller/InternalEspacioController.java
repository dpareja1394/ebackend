package co.edu.usbcali.parqueaderos.controller;

import co.edu.usbcali.parqueaderos.dto.EspacioResponse;
import co.edu.usbcali.parqueaderos.dto.OcupacionRequest;
import co.edu.usbcali.parqueaderos.service.OcupacionService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** API interna de ocupación, usada solo por vehiculos-service; el gateway no la enruta. */
@RestController
@RequestMapping("/internal/v1/espacios/{espacioId}/ocupacion")
public class InternalEspacioController {

    private final OcupacionService service;

    public InternalEspacioController(OcupacionService service) {
        this.service = service;
    }

    @PostMapping
    public EspacioResponse ocupar(@PathVariable UUID espacioId, @Valid @RequestBody OcupacionRequest request) {
        return service.ocupar(espacioId, request);
    }

    @DeleteMapping
    public ResponseEntity<Void> liberar(@PathVariable UUID espacioId, @RequestParam UUID vehiculoId) {
        service.liberar(espacioId, vehiculoId);
        return ResponseEntity.noContent().build();
    }
}
