package co.edu.usbcali.parqueaderos.dto;

import co.edu.usbcali.parqueaderos.domain.TipoVehiculo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EspacioRequest(
        @NotBlank(message = "Es obligatorio") @Size(min = 1, max = 20, message = "Debe tener entre 1 y 20 caracteres")
        String codigo,
        @NotNull(message = "Es obligatorio")
        TipoVehiculo tipoVehiculo) {
}
