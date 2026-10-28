package co.edu.usbcali.dnetwork_java.controller;

import co.edu.usbcali.dnetwork_java.dto.response.ObtenerUsuarioResponse;
import co.edu.usbcali.dnetwork_java.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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
        return usuarioService.obtenerUsuarios();
    }

    @GetMapping("/{id}")
    ResponseEntity<ObtenerUsuarioResponse> obtenerUsuarioPorId(@PathVariable Integer id) throws Exception {
        return ResponseEntity.ok(usuarioService.obtenerUsuarioPorId(id));
    }

    @DeleteMapping("/eliminar/{id}")
    ResponseEntity<Void> eliminarUsuario(@PathVariable Integer id) throws Exception {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/activar/{id}")
    ResponseEntity<ObtenerUsuarioResponse> activarUsuario(@PathVariable Integer id) throws Exception {
        return ResponseEntity.ok(usuarioService.activarUsuario(id));
    }

    @PutMapping("/desactivar/{id}")
    ResponseEntity<ObtenerUsuarioResponse> desactivarUsuario(@PathVariable Integer id) throws Exception {
        return ResponseEntity.ok(usuarioService.desactivarUsuario(id));
    }
}
