package co.edu.usbcali.parqueaderos.dto;

import co.edu.usbcali.parqueaderos.domain.Espacio;
import co.edu.usbcali.parqueaderos.domain.EstadoEspacio;
import co.edu.usbcali.parqueaderos.domain.TipoVehiculo;
import java.util.UUID;

public record EspacioResponse(
        UUID id,
        UUID parqueaderoId,
        String codigo,
        TipoVehiculo tipoVehiculo,
        EstadoEspacio estado,
        UUID vehiculoId) {

    public static EspacioResponse de(Espacio e) {
        return new EspacioResponse(e.getId(), e.getParqueaderoId(), e.getCodigo(), e.getTipoVehiculo(),
                e.getEstado(), e.getVehiculoId());
    }
}
