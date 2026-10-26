package co.edu.usbcali.parqueaderos.dto;

import co.edu.usbcali.parqueaderos.domain.TipoVehiculo;
import java.util.Map;
import java.util.UUID;

/** Disponibilidad de un parqueadero, en total y por tipo de vehículo (FR-009). */
public record ResumenResponse(UUID parqueaderoId, ConteoEspacios general, Map<TipoVehiculo, ConteoEspacios> porTipo) {
}
