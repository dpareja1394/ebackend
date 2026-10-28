package co.edu.usbcali.dnetwork_java.controller;

import co.edu.usbcali.dnetwork_java.dto.response.ObtenerUsuarioResponse;
import co.edu.usbcali.dnetwork_java.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/usuarios")
public class UsuarioController {

    // Inyección de dependencias
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/ping")
    String pingPong() {
        return "pong";
    }

    @GetMapping("/validar-estado")
    String validarEstado() {
        return "ok";
    }

    @GetMapping("/obtener-usuarios")
    List<ObtenerUsuarioResponse> obtenerUsuarios() {
        log.info("Petición GET /usuarios/obtener-usuarios");
        return usuarioService.obtenerUsuarios();
    }

    @GetMapping("/{id}")
    ResponseEntity<ObtenerUsuarioResponse> obtenerUsuarioPorId(@PathVariable Integer id) {
        log.info("Petición GET /usuarios/{}", id);
        try {
            return ResponseEntity.ok(usuarioService.obtenerUsuarioPorId(id));
        } catch (Exception e) {
            log.error("Error en GET /usuarios/{}: {}", id, e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/eliminar/{id}")
    ResponseEntity<Void> eliminarUsuario(@PathVariable Integer id) {
        log.info("Petición DELETE /usuarios/eliminar/{}", id);
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error en DELETE /usuarios/eliminar/{}: {}", id, e.getMessage());
            throw e;
        }
    }

}
