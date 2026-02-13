package com.angularSek.DemoSek.service;

import com.angularSek.DemoSek.dto.InvitadoRequestDTO;
import com.angularSek.DemoSek.dto.InvitadoResponseDTO;

import java.util.List;

public interface InvitadoService {

    List<InvitadoResponseDTO> getInvitados();

    InvitadoResponseDTO findInvitadoById(Integer id);

    InvitadoResponseDTO saveInvitado(InvitadoRequestDTO dto) throws Exception;
}

