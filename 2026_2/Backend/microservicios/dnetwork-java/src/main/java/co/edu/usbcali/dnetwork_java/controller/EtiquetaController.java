package co.edu.usbcali.dnetwork_java.controller;

import co.edu.usbcali.dnetwork_java.dto.request.CrearEtiquetaRequest;
import co.edu.usbcali.dnetwork_java.dto.response.ObtenerEtiquetaResponse;
import co.edu.usbcali.dnetwork_java.service.EtiquetaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/etiquetas")
public class EtiquetaController {

    // Inyección de dependencias hacia el Service
    @Autowired
    private EtiquetaService etiquetaService;

    @GetMapping("/ping")
    String pingPong() {
        return "pong";
    }

    @GetMapping("/validar-estado")
    String validarEstado() {
        return "ok";
    }

    @GetMapping("/obtener-etiquetas")
    List<ObtenerEtiquetaResponse> obtenerEtiquetas() {
        log.info("Petición GET /etiquetas/obtener-etiquetas");
        return etiquetaService.obtenerEtiquetas();
    }

    @GetMapping("/{id}")
    ResponseEntity<ObtenerEtiquetaResponse> obtenerEtiquetaPorId(@PathVariable Integer id) {
        log.info("Petición GET /etiquetas/{}", id);
        try {
            return ResponseEntity.ok(etiquetaService.obtenerEtiquetaPorId(id));
        } catch (Exception e) {
            log.error("Error en GET /etiquetas/{}: {}", id, e.getMessage());
            throw e;
        }
    }

    @PostMapping("/crear")
    ResponseEntity<ObtenerEtiquetaResponse> crearEtiqueta(@RequestBody CrearEtiquetaRequest etiquetaRequest) {
        log.info("Petición POST /etiquetas/crear con body: {}", etiquetaRequest);
        try {
            ObtenerEtiquetaResponse etiquetaResponse =
                etiquetaService.crearEtiqueta(etiquetaRequest);
            return new ResponseEntity<>(etiquetaResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error en POST /etiquetas/crear: {}", e.getMessage());
            throw e;
        }
    }
}
