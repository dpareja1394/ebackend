package co.edu.usbcali.dnetwork_java.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TipoMedio implements IValorDbEnum {
    IMAGEN("imagen"),
    VIDEO("video");

    private final String valorDb;

    public static TipoMedio getTipoMedio(String valorDb) {
        for (TipoMedio tipoMedio : TipoMedio.values()) {
            if (tipoMedio.getValorDb().equalsIgnoreCase(valorDb)) {
                return tipoMedio;
            }
        }
        return null;
    }
}
