package co.edu.usbcali.autosusbcali.controller;

import co.edu.usbcali.autosusbcali.domain.Rol;
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
    public List<Rol> obtenerTodos() {
        return rolRepository.findAll();
    }

    @GetMapping("/activos")
    public List<Rol> obtenerTodosActivos() {
        return rolRepository.findByActivo(true);
    }

    @GetMapping("/inactivos")
    public List<Rol> obtenerTodosInactivos() {
        return rolRepository.findByActivo(false);
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
