package co.edu.usbcali.autosusbcali.controller;

import co.edu.usbcali.autosusbcali.domain.Rol;
import co.edu.usbcali.autosusbcali.repository.RolRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@AllArgsConstructor
public class RolController {

    private final RolRepository rolRepository;

    @GetMapping
    public ResponseEntity<List<Rol>> obtenerTodos() {
        return ResponseEntity.ok(rolRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rol> obtenerPorId(@PathVariable Long id) {
        return rolRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Rol> crear(@RequestBody Rol rol) {
        rol.setId(null);
        return ResponseEntity.ok(rolRepository.save(rol));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rol> actualizar(@PathVariable Long id, @RequestBody Rol rol) {
        return rolRepository.findById(id)
                .map(rolExistente -> {
                    rol.setId(id);
                    return ResponseEntity.ok(rolRepository.save(rol));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!rolRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        rolRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
