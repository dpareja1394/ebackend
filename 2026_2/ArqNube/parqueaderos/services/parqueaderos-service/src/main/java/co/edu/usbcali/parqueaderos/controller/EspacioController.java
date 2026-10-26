package co.edu.usbcali.parqueaderos.controller;

import co.edu.usbcali.parqueaderos.domain.EstadoEspacio;
import co.edu.usbcali.parqueaderos.domain.TipoVehiculo;
import co.edu.usbcali.parqueaderos.dto.EspacioRequest;
import co.edu.usbcali.parqueaderos.dto.EspacioResponse;
import co.edu.usbcali.parqueaderos.dto.EspacioUpdateRequest;
import co.edu.usbcali.parqueaderos.dto.PaginaResponse;
import co.edu.usbcali.parqueaderos.dto.Paginacion;
import co.edu.usbcali.parqueaderos.dto.ResumenResponse;
import co.edu.usbcali.parqueaderos.service.EspacioService;
import co.edu.usbcali.parqueaderos.service.OcupacionService;
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
@RequestMapping("/api/v1")
public class EspacioController {

    private final EspacioService service;
    private final OcupacionService ocupacionService;

    public EspacioController(EspacioService service, OcupacionService ocupacionService) {
        this.service = service;
        this.ocupacionService = ocupacionService;
    }

    @GetMapping("/parqueaderos/{parqueaderoId}/espacios")
    public PaginaResponse<EspacioResponse> listar(
            @PathVariable UUID parqueaderoId,
            @RequestParam(required = false) EstadoEspacio estado,
            @RequestParam(required = false) TipoVehiculo tipoVehiculo,
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer tamano) {
        return service.listar(parqueaderoId, estado, tipoVehiculo, Paginacion.de(pagina, tamano));
    }

    @PostMapping("/parqueaderos/{parqueaderoId}/espacios")
    public ResponseEntity<EspacioResponse> crear(@PathVariable UUID parqueaderoId,
            @Valid @RequestBody EspacioRequest request) {
        EspacioResponse creado = service.crear(parqueaderoId, request);
        return ResponseEntity.created(URI.create("/api/v1/espacios/" + creado.id())).body(creado);
    }

    @GetMapping("/parqueaderos/{parqueaderoId}/resumen")
    public ResumenResponse resumen(@PathVariable UUID parqueaderoId) {
        return service.resumen(parqueaderoId);
    }

    @GetMapping("/espacios/{espacioId}")
    public EspacioResponse obtener(@PathVariable UUID espacioId) {
        return service.obtener(espacioId);
    }

    @PutMapping("/espacios/{espacioId}")
    public EspacioResponse actualizar(@PathVariable UUID espacioId, @Valid @RequestBody EspacioUpdateRequest request) {
        return service.actualizar(espacioId, request);
    }

    @DeleteMapping("/espacios/{espacioId}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID espacioId) {
        service.eliminar(espacioId);
        return ResponseEntity.noContent().build();
    }

    /** Liberación forzada de un espacio huérfano; el sistema verifica antes de liberar (FR-007). */
    @DeleteMapping("/espacios/{espacioId}/ocupacion")
    public EspacioResponse forzarLiberacion(@PathVariable UUID espacioId) {
        return ocupacionService.forzarLiberacion(espacioId);
    }
}
