package co.edu.usbcali.aerolineaplus.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListarCiudadesResponse {
    private Integer id;
    private String nombre;
    private String descripcion;
    
    @JsonProperty("nombre_pais")
    private String nombrePais;
}
