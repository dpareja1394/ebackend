package co.edu.usbcali.autosusbcali.controller;

import co.edu.usbcali.autosusbcali.dto.response.ObtenerUsuarioResponse;
import co.edu.usbcali.autosusbcali.service.IUsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@AllArgsConstructor
public class UsuarioController {
    private final IUsuarioService usuarioService;

    @GetMapping
    public List<ObtenerUsuarioResponse> obtenerUsuarios() {
        return usuarioService.obtenerUsuarios();
    }

    @GetMapping("/por-id/{id}")
    public ResponseEntity<ObtenerUsuarioResponse> obtenerPorId(@PathVariable Long id) throws Exception {
        return new ResponseEntity<>(
                usuarioService.obtenerUsuarioPorId(id),
                HttpStatus.OK
        );
    }

}
