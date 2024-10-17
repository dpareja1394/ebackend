package co.edu.usbcali.aerolineaplus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaisDTO {
    private Integer id;
    private String codigo;
    private String nombre;
    private String descripcion;
}
