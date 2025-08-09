package co.edu.usbcali.aerolineaplus.controller;

import co.edu.usbcali.aerolineaplus.dto.CiudadDTO;
import co.edu.usbcali.aerolineaplus.dto.request.CreateCiudadRequest;
import co.edu.usbcali.aerolineaplus.dto.response.ListarCiudadesResponse;
import co.edu.usbcali.aerolineaplus.service.CiudadService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ciudad")
public class CiudadController {

    //Inyección de dependencias hacia CiudadService
    private final CiudadService ciudadService;


    public CiudadController(CiudadService ciudadService) {
        this.ciudadService = ciudadService;
    }

    @PostMapping(value = "/guardarNuevaCiudad")
    public ResponseEntity<CiudadDTO> guardarNuevaCiudad(@RequestBody @Valid CreateCiudadRequest createCiudadRequest) throws Exception {
        CiudadDTO ciudadResponse = ciudadService.guardarNuevaCiudad(createCiudadRequest);
        return new ResponseEntity<>(ciudadResponse, HttpStatus.CREATED);
    }

    @GetMapping(value = "/obtenerCiudades")
    public List<ListarCiudadesResponse> obtenerCiudades() {
        return ciudadService.obtenerCiudades();
    }

    @GetMapping(value = "/buscarCiudadPorId/{id}")
    public ResponseEntity<CiudadDTO> buscarCiudadPorId(@PathVariable Integer id) throws Exception {
        CiudadDTO ciudadResponse = ciudadService.buscarCiudadPorId(id);
        return new ResponseEntity<>(ciudadResponse, HttpStatus.OK);
    }

    @PutMapping(value = "/modificarCiudad")
    public ResponseEntity<CiudadDTO> modificarCiudad(@RequestBody CiudadDTO ciudadDTO) throws Exception {
        CiudadDTO ciudadResponse = ciudadService.modificarCiudad(ciudadDTO);
        return new ResponseEntity<>(ciudadResponse, HttpStatus.OK);
    }

    @DeleteMapping(value = "/eliminarCiudad/{id}")
    public ResponseEntity<Void> eliminarCiudad(@PathVariable("id") Integer idCiudad) throws Exception {
        ciudadService.eliminarCiudad(idCiudad);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
