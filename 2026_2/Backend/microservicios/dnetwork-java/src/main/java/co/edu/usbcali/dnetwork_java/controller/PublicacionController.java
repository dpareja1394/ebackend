package co.edu.usbcali.dnetwork_java.controller;

import co.edu.usbcali.dnetwork_java.dto.request.CrearPublicacionRequest;
import co.edu.usbcali.dnetwork_java.dto.request.ModificarPublicacionRequest;
import co.edu.usbcali.dnetwork_java.dto.response.ObtenerPublicacionResponse;
import co.edu.usbcali.dnetwork_java.dto.response.ObtenerUsuarioResponse;
import co.edu.usbcali.dnetwork_java.service.PublicacionService;
import org.apache.coyote.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/publicaciones")
public class PublicacionController {

    @Autowired
    private PublicacionService publicacionService;

    @GetMapping
    List<ObtenerPublicacionResponse> obtenerPublicaciones() {
        log.info("Petición GET /publicaciones");
        return publicacionService.obtenerPublicaciones();
    }

    @GetMapping("/{id}")
    ResponseEntity<ObtenerPublicacionResponse> obtenerPublicacionPorId(@PathVariable Integer id) throws Exception {
        log.info("Petición GET /publicaciones/{}", id);
        try {
            return ResponseEntity.ok(publicacionService.obtenerPublicacionPorId(id));
        } catch (Exception e) {
            log.error("Error en GET /publicaciones/{}: {}", id, e.getMessage());
            throw e;
        }
    }

    @PostMapping("/crear")
    ResponseEntity<ObtenerPublicacionResponse> crearPublicacion(@RequestBody CrearPublicacionRequest crearPublicacionRequest) throws Exception {
        log.info("Petición POST /publicaciones/crear con body: {}", crearPublicacionRequest);
        try {
            return new ResponseEntity<>(publicacionService.crearPublicacion(crearPublicacionRequest), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error en POST /publicaciones/crear: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/modificar/{id}")
    ResponseEntity<ObtenerPublicacionResponse> modificarPublicacion(
            @PathVariable Integer id,
            @RequestBody ModificarPublicacionRequest modificarPublicacionRequest) throws Exception {
        log.info("Petición PUT /publicaciones/modificar/{} con body: {}", id, modificarPublicacionRequest);
        try {
            return ResponseEntity.ok(
                    publicacionService.modificarPublicacion(id, modificarPublicacionRequest)
            );
        } catch (Exception e) {
            log.error("Error en PUT /publicaciones/modificar/{}: {}", id, e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/eliminar/{id}")
    ResponseEntity<Void> eliminarPublicacion(@PathVariable Integer id) throws Exception {
        log.info("Petición DELETE /publicaciones/eliminar/{}", id);
        try {
            publicacionService.eliminarPublicacion(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error en DELETE /publicaciones/eliminar/{}: {}", id, e.getMessage());
            throw e;
        }
    }

}
