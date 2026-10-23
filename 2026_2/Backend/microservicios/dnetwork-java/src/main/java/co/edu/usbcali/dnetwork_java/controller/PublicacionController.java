package co.edu.usbcali.dnetwork_java.controller;

import co.edu.usbcali.dnetwork_java.dto.request.CrearPublicacionRequest;
import co.edu.usbcali.dnetwork_java.dto.request.ModificarPublicacionRequest;
import co.edu.usbcali.dnetwork_java.dto.response.ObtenerPublicacionResponse;
import co.edu.usbcali.dnetwork_java.dto.response.ObtenerUsuarioResponse;
import co.edu.usbcali.dnetwork_java.service.PublicacionService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/publicaciones")
public class PublicacionController {

    @Autowired
    private PublicacionService publicacionService;

    @GetMapping
    List<ObtenerPublicacionResponse> obtenerPublicaciones() {
        return publicacionService.obtenerPublicaciones();
    }

    @GetMapping("/{id}")
    ResponseEntity<ObtenerPublicacionResponse> obtenerPublicacionPorId(@PathVariable Integer id) throws Exception {
        return ResponseEntity.ok(publicacionService.obtenerPublicacionPorId(id));
    }

    @PostMapping("/crear")
    ResponseEntity<ObtenerPublicacionResponse> crearPublicacion(@RequestBody CrearPublicacionRequest crearPublicacionRequest) throws Exception {
        return new ResponseEntity<>(publicacionService.crearPublicacion(crearPublicacionRequest), HttpStatus.CREATED);
    }

    @PutMapping("/modificar/{id}")
    ResponseEntity<ObtenerPublicacionResponse> modificarPublicacion(
            @PathVariable Integer id,
            @RequestBody ModificarPublicacionRequest modificarPublicacionRequest) throws Exception {
        return ResponseEntity.ok(
                publicacionService.modificarPublicacion(id, modificarPublicacionRequest)
        );
    }

    @DeleteMapping("/eliminar/{id}")
    ResponseEntity<Void> eliminarPublicacion(@PathVariable Integer id) throws Exception {
        publicacionService.eliminarPublicacion(id);
        return ResponseEntity.noContent().build();
    }

}
