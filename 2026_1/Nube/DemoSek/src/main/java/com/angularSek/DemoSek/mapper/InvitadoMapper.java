package com.angularSek.DemoSek.mapper;

import com.angularSek.DemoSek.domain.Invitado;
import com.angularSek.DemoSek.dto.InvitadoRequestDTO;
import com.angularSek.DemoSek.dto.InvitadoResponseDTO;

import java.util.List;

public class InvitadoMapper {

    public static InvitadoResponseDTO domainToResponseDTO(Invitado domain) {

        return InvitadoResponseDTO.builder()
                .idInvitado(domain.getIdInvitado())
                .nombre(domain.getNombre())
                .email(domain.getEmail())
                .telefono(domain.getTelefono())
                .fechaRegistro(domain.getFechaRegistro())
                .build();
    }

    public static List<InvitadoResponseDTO> domainListToResponseList(List<Invitado> list) {
        return list.stream()
                .map(InvitadoMapper::domainToResponseDTO)
                .toList();
    }

    public static Invitado requestToDomain(InvitadoRequestDTO dto) {

        return Invitado.builder()
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .build();
    }
}

