package co.edu.usbcali.dnetwork_java.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TipoNotificacion implements IValorDbEnum {
    NUEVO_SEGUIDOR("nuevo_seguidor"),
    SOLICITUD_SEGUIMIENTO("solicitud_seguimiento"),
    LIKE("like"),
    DISLIKE("dislike"),
    COMENTARIO("comentario"),
    RESPUESTA_COMENTARIO("respuesta_comentario");

    private final String valorDb;

    public static TipoNotificacion getTipoNotificacion(String valorDb) {
        for (TipoNotificacion tipoNotificacion : TipoNotificacion.values()) {
            if (tipoNotificacion.getValorDb().equalsIgnoreCase(valorDb)) {
                return tipoNotificacion;
            }
        }
        return null;
    }
}
