package com.angularSek.DemoSek.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvitadoRequestDTO {

    private String nombre;
    private String email;
    private String telefono;

}
