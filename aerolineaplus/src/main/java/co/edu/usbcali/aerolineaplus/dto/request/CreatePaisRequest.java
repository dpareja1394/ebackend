package co.edu.usbcali.aerolineaplus.dto.request;

import co.edu.usbcali.aerolineaplus.util.validation.PaisValidate;
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
public class CreatePaisRequest {

    @NotNull(message = PaisValidate.CODIGO_NOT_NULL)
    @NotBlank(message = PaisValidate.CODIGO_NOT_EMPTY)
    @Pattern(regexp = PaisValidate.CODIGO_REGEX, message = PaisValidate.CODIGO_NOT_VALID)
    private String codigo;
    
    @NotNull(message = PaisValidate.NOMBRE_NOT_NULL)
    @NotBlank(message = PaisValidate.NOMBRE_NOT_EMPTY)
    @Pattern(regexp = PaisValidate.NOMBRE_REGEX, message = PaisValidate.NOMBRE_NOT_VALID)
    private String nombre;
    
    private String descripcion;

}
