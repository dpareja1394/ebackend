package co.edu.usbcali.autosusbcali.controller;

import co.edu.usbcali.autosusbcali.domain.Rol;
import co.edu.usbcali.autosusbcali.repository.RolRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@AllArgsConstructor
public class RolController {
    private final RolRepository rolRepository;

    @GetMapping
    public List<Rol> obtenerTodos() {
        return rolRepository.findAll();
    }


}
