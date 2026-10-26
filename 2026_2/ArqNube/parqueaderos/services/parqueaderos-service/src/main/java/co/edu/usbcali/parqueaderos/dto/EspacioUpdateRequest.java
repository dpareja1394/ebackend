package co.edu.usbcali.parqueaderos.dto;

import co.edu.usbcali.parqueaderos.domain.EstadoEspacio;
import co.edu.usbcali.parqueaderos.domain.TipoVehiculo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EspacioUpdateRequest(
        @NotBlank(message = "Es obligatorio") @Size(min = 1, max = 20, message = "Debe tener entre 1 y 20 caracteres")
        String codigo,
        @NotNull(message = "Es obligatorio")
        TipoVehiculo tipoVehiculo,
        @NotNull(message = "Es obligatorio")
        EstadoEspacio estado) {

    /** El estado OCUPADO solo lo asigna el sistema al ubicar un vehículo (FR-007). */
    @JsonIgnore
    @AssertTrue(message = "El estado OCUPADO es gestionado por el sistema")
    public boolean isEstado() {
        return estado != EstadoEspacio.OCUPADO;
    }
}
