package co.edu.usbcali.dnetwork_java.controller;

import co.edu.usbcali.dnetwork_java.domain.Etiqueta;
import co.edu.usbcali.dnetwork_java.repository.EtiquetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/etiquetas")
public class EtiquetaController {

    // Inyección de dependencias
    @Autowired
    private EtiquetaRepository etiquetaRepository;

    @GetMapping("/ping")
    String pingPong() {
        return "pong";
    }

    @GetMapping("/validar-estado")
    String validarEstado() {
        return "ok";
    }

    @GetMapping("/obtener-etiquetas")
    List<Etiqueta> obtenerEtiquetas() {
        return etiquetaRepository.findAll();
    }

    @GetMapping("/{id}")
    ResponseEntity<Etiqueta> obtenerEtiquetaPorId(@PathVariable Integer id) {
        Etiqueta etiqueta = etiquetaRepository.findById(id).orElse(null);
        return ResponseEntity.ok(etiqueta);
    }
}
