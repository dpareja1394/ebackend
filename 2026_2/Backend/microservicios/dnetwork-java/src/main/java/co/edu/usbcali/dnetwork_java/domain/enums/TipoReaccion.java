package co.edu.usbcali.dnetwork_java.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TipoReaccion implements IValorDbEnum {
    LIKE("like"),
    DISLIKE("dislike");

    private final String valorDb;

    public static TipoReaccion getTipoReaccion(String valorDb) {
        for (TipoReaccion tipoReaccion : TipoReaccion.values()) {
            if (tipoReaccion.getValorDb().equalsIgnoreCase(valorDb)) {
                return tipoReaccion;
            }
        }
        return null;
    }
}
