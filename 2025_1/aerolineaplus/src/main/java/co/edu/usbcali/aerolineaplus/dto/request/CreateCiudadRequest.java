package co.edu.usbcali.aerolineaplus.dto.request;

import co.edu.usbcali.aerolineaplus.util.validation.CiudadValidate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCiudadRequest {
    
    @NotNull(message = CiudadValidate.NOMBRE_NOT_NULL)
    @NotBlank(message = CiudadValidate.NOMBRE_NOT_EMPTY)
    @Pattern(regexp = CiudadValidate.NOMBRE_REGEX, message = CiudadValidate.NOMBRE_NOT_VALID)
    private String nombre;
    
    @NotNull(message = CiudadValidate.DESCRIPCION_NOT_NULL)
    @NotBlank(message = CiudadValidate.DESCRIPCION_NOT_EMPTY)
    @Pattern(regexp = CiudadValidate.DESCRIPCION_REGEX, message = CiudadValidate.DESCRIPCION_NOT_VALID)
    private String descripcion;
    
    @NotNull(message = CiudadValidate.PAIS_NOT_NULL)
    @Min(message = CiudadValidate.PAIS_NOT_ZERO, value = 1)
    private Integer paisId;

}
