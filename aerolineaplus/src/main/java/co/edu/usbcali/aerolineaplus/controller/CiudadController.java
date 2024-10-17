package co.edu.usbcali.aerolineaplus.controller;

import co.edu.usbcali.aerolineaplus.domain.Ciudad;
import co.edu.usbcali.aerolineaplus.dto.CiudadDTO;
import co.edu.usbcali.aerolineaplus.mapper.CiudadMapper;
import co.edu.usbcali.aerolineaplus.repository.CiudadRepository;
import co.edu.usbcali.aerolineaplus.service.CiudadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ciudad")
public class CiudadController {

    //Inyección de dependencias hacia CiudadService
    private final CiudadRepository ciudadRepository;
    private final CiudadService ciudadService;


    public CiudadController(CiudadRepository ciudadRepository, CiudadService ciudadService) {
        this.ciudadRepository = ciudadRepository;
        this.ciudadService = ciudadService;
    }

    @PostMapping(value = "/guardarNuevaCiudad")
    public ResponseEntity<CiudadDTO> guardarNuevaCiudad(@RequestBody CiudadDTO ciudadDTO) throws Exception {
        CiudadDTO ciudadResponse = ciudadService.guardarNuevaCiudad(ciudadDTO);
        return new ResponseEntity<>(ciudadResponse, HttpStatus.CREATED);
    }

    @GetMapping(value = "/obtenerCiudades")
    public List<CiudadDTO> obtenerCiudades() {
        List<Ciudad> listaCiudades = ciudadRepository.findAll();
        return CiudadMapper.domainToDTOList(listaCiudades);
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
}
