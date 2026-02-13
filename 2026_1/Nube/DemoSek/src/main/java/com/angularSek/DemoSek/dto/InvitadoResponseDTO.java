package com.angularSek.DemoSek.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
public class InvitadoResponseDTO {

    private Integer idInvitado;
    private String nombre;
    private String email;
    private String telefono;
    private Date fechaRegistro;

}

