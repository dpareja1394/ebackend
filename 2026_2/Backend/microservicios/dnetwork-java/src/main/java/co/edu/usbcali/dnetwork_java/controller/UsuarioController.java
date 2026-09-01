package co.edu.usbcali.dnetwork_java.controller;

import co.edu.usbcali.dnetwork_java.domain.Usuario;
import co.edu.usbcali.dnetwork_java.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    // Inyección de dependencias
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/ping")
    String pingPong() {
        return "pong";
    }

    @GetMapping("/validar-estado")
    String validarEstado() {
        return "ok";
    }

    @GetMapping("/obtener-usuarios")
    List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }
}
