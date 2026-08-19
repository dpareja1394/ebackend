package co.edu.usbcali.dnetwork_java.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PrivacidadPublicacion {
    PUBLICO("publico"),
    SEGUIDORES("seguidores"),
    PRIVADO("privado");

    private final String valorDb;

    public static PrivacidadPublicacion getPrivacidadPublicacion(String valorDb) {
        for (PrivacidadPublicacion privacidadPublicacion : PrivacidadPublicacion.values()) {
            if (privacidadPublicacion.getValorDb().equalsIgnoreCase(valorDb)) {
                return privacidadPublicacion;
            }
        }
        return null;
    }
}
