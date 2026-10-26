package co.edu.usbcali.parqueaderos.dto;

import co.edu.usbcali.parqueaderos.domain.TipoVehiculo;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record OcupacionRequest(
        @NotNull(message = "Es obligatorio") UUID vehiculoId,
        @NotNull(message = "Es obligatorio") UUID parqueaderoId,
        @NotNull(message = "Es obligatorio") TipoVehiculo tipoVehiculo) {
}
