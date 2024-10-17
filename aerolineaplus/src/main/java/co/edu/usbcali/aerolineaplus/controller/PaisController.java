package co.edu.usbcali.aerolineaplus.controller;


import co.edu.usbcali.aerolineaplus.dto.PaisDTO;
import co.edu.usbcali.aerolineaplus.service.PaisService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pais")
public class PaisController {
    //Inyección de dependencias
    private PaisService paisService;

    public PaisController(PaisService paisService) {
        this.paisService = paisService;
    }

    @GetMapping(value = "/obtenerPaises")
    public List<PaisDTO> obtenerPaises() {
        return paisService.obtenerPaises();
    }

    @PostMapping(value = "/crearNuevoPais")
    public ResponseEntity<PaisDTO> crearNuevoPais(@RequestBody PaisDTO paisDTO) throws Exception {
        PaisDTO paisResponse = paisService.guardarNuevoPais(paisDTO);
        return new ResponseEntity<>(paisResponse, HttpStatus.CREATED);
    }

    @GetMapping(value = "/buscarPaisPorId/{id}")
    public ResponseEntity<PaisDTO> buscarPaisPorId(@PathVariable("id") Integer id) throws Exception {
        PaisDTO paisDTO = paisService.buscarPaisPorId(id);
        return new ResponseEntity<>(paisDTO, HttpStatus.OK);
    }

    @PutMapping(value = "/modificarPais")
    public ResponseEntity<PaisDTO> modificarPais(@RequestBody PaisDTO paisDTO) throws Exception {
        PaisDTO paisResponse = paisService.modificarPais(paisDTO);
        return new ResponseEntity<>(paisResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/buscarPaisPorCodigo/{codigo}")
    public ResponseEntity<PaisDTO> buscarPaisPorCodigo(@PathVariable("codigo") String codigo) throws Exception {
        PaisDTO paisDTO = paisService.buscarPaisPorCodigo(codigo);
        return new ResponseEntity<>(paisDTO, HttpStatus.OK);
    }
}
