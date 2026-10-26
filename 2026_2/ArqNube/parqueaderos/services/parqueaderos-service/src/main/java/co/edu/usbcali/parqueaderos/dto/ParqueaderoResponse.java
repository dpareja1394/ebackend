package co.edu.usbcali.parqueaderos.dto;

import co.edu.usbcali.parqueaderos.domain.EstadoParqueadero;
import co.edu.usbcali.parqueaderos.domain.Parqueadero;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ParqueaderoResponse(
        UUID id,
        String nombre,
        String direccion,
        String ciudad,
        boolean abierto24h,
        @JsonFormat(pattern = "HH:mm") LocalTime horaApertura,
        @JsonFormat(pattern = "HH:mm") LocalTime horaCierre,
        EstadoParqueadero estado,
        long totalEspacios,
        OffsetDateTime creadoEn,
        OffsetDateTime actualizadoEn) {

    public static ParqueaderoResponse de(Parqueadero p, long totalEspacios) {
        return new ParqueaderoResponse(p.getId(), p.getNombre(), p.getDireccion(), p.getCiudad(),
                p.isAbierto24h(), p.getHoraApertura(), p.getHoraCierre(), p.getEstado(), totalEspacios,
                p.getCreadoEn(), p.getActualizadoEn());
    }
}
