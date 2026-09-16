package co.edu.usbcali.dnetwork_java.controller;

import co.edu.usbcali.dnetwork_java.dto.request.CrearEtiquetaRequest;
import co.edu.usbcali.dnetwork_java.dto.response.ObtenerEtiquetaResponse;
import co.edu.usbcali.dnetwork_java.service.EtiquetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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
        return etiquetaService.obtenerEtiquetas();
    }

    @GetMapping("/{id}")
    ResponseEntity<ObtenerEtiquetaResponse> obtenerEtiquetaPorId(@PathVariable Integer id) throws Exception {
        return ResponseEntity.ok(etiquetaService.obtenerEtiquetaPorId(id));
    }

    @PostMapping("/crear")
    ResponseEntity<ObtenerEtiquetaResponse> crearEtiqueta(@RequestBody CrearEtiquetaRequest etiquetaRequest) throws Exception {
        ObtenerEtiquetaResponse etiquetaResponse =
            etiquetaService.crearEtiqueta(etiquetaRequest);
        return new ResponseEntity<>(etiquetaResponse, HttpStatus.CREATED);
    }
}
