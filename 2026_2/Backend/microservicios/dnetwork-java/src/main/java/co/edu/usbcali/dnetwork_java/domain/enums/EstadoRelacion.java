package co.edu.usbcali.dnetwork_java.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EstadoRelacion implements IValorDbEnum {
    PENDIENTE("pendiente"),
    ACEPTADA("aceptada"),
    RECHAZADA("rechazada");

    private final String valorDb;

    public static EstadoRelacion getEstadoRelacion(String valorDb) {
        for (EstadoRelacion estadoRelacion : EstadoRelacion.values()) {
            if (estadoRelacion.getValorDb().equalsIgnoreCase(valorDb)) {
                return estadoRelacion;
            }
        }
        return null;
    }
}
