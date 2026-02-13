package com.angularSek.DemoSek.controller;

import com.angularSek.DemoSek.dto.InvitadoRequestDTO;
import com.angularSek.DemoSek.dto.InvitadoResponseDTO;
import com.angularSek.DemoSek.service.InvitadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/invitados")
@RestController
@RequiredArgsConstructor
public class InvitadoController {

    private final InvitadoService invitadoService;

    @GetMapping("/all")
    List<InvitadoResponseDTO> getAll() {
        return invitadoService.getInvitados();
    }

    @GetMapping("/{id}")
    InvitadoResponseDTO getById(@PathVariable Integer id) {
        return invitadoService.findInvitadoById(id);
    }

    @PostMapping
    ResponseEntity<InvitadoResponseDTO> save(@RequestBody InvitadoRequestDTO dto) throws Exception {
        InvitadoResponseDTO response = invitadoService.saveInvitado(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

