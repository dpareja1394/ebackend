package co.edu.usbcali.autosusbcali.controller;

import co.edu.usbcali.autosusbcali.domain.Rol;
import co.edu.usbcali.autosusbcali.dto.response.ObtenerRolResponse;
import co.edu.usbcali.autosusbcali.mapper.RolMapper;
import co.edu.usbcali.autosusbcali.repository.RolRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
@AllArgsConstructor
public class RolController {
    private final RolRepository rolRepository;

    @GetMapping
    public List<ObtenerRolResponse> obtenerTodos() {
        return RolMapper.listaRolesAListaObtenerRolResponse(rolRepository.findAll());
    }

    @GetMapping("/activos")
    public List<ObtenerRolResponse> obtenerTodosActivos() {
        return RolMapper.listaRolesAListaObtenerRolResponse(rolRepository.findByActivo(true));
    }

    @GetMapping("/inactivos")
    public List<ObtenerRolResponse> obtenerTodosInactivos() {
        return RolMapper.listaRolesAListaObtenerRolResponse(rolRepository.findByActivo(false));
    }

    @GetMapping("/por-nombre/{nombre}")
    public ResponseEntity<Rol> obtenerPorNombre(@PathVariable String nombre) {
        Optional<Rol> rolOptional = rolRepository.findByNombre(nombre);
        if (rolOptional.isPresent()) {
            return new ResponseEntity<>(rolOptional.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/por-id/{id}")
    public ResponseEntity<Rol> obtenerPorId(@PathVariable Long id) {
        Optional<Rol> rolOptional = rolRepository.findById(id);
        if (rolOptional.isPresent()) {
            return new ResponseEntity<>(rolOptional.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }



}
