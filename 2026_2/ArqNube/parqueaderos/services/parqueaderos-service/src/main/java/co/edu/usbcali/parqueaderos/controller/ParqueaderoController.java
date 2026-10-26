package co.edu.usbcali.parqueaderos.controller;

import co.edu.usbcali.parqueaderos.domain.EstadoParqueadero;
import co.edu.usbcali.parqueaderos.dto.PaginaResponse;
import co.edu.usbcali.parqueaderos.dto.Paginacion;
import co.edu.usbcali.parqueaderos.dto.ParqueaderoRequest;
import co.edu.usbcali.parqueaderos.dto.ParqueaderoResponse;
import co.edu.usbcali.parqueaderos.dto.ParqueaderoUpdateRequest;
import co.edu.usbcali.parqueaderos.service.ParqueaderoService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/parqueaderos")
public class ParqueaderoController {

    private final ParqueaderoService service;

    public ParqueaderoController(ParqueaderoService service) {
        this.service = service;
    }

    @GetMapping
    public PaginaResponse<ParqueaderoResponse> listar(
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) EstadoParqueadero estado,
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer tamano) {
        return service.listar(ciudad, estado, Paginacion.de(pagina, tamano));
    }

    @PostMapping
    public ResponseEntity<ParqueaderoResponse> crear(@Valid @RequestBody ParqueaderoRequest request) {
        ParqueaderoResponse creado = service.crear(request);
        return ResponseEntity.created(URI.create("/api/v1/parqueaderos/" + creado.id())).body(creado);
    }

    @GetMapping("/{parqueaderoId}")
    public ParqueaderoResponse obtener(@PathVariable UUID parqueaderoId) {
        return service.obtener(parqueaderoId);
    }

    @PutMapping("/{parqueaderoId}")
    public ParqueaderoResponse actualizar(@PathVariable UUID parqueaderoId,
            @Valid @RequestBody ParqueaderoUpdateRequest request) {
        return service.actualizar(parqueaderoId, request);
    }

    @DeleteMapping("/{parqueaderoId}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID parqueaderoId) {
        service.eliminar(parqueaderoId);
        return ResponseEntity.noContent().build();
    }
}
