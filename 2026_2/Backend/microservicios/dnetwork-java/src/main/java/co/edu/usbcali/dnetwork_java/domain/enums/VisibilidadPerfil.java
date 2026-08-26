package co.edu.usbcali.dnetwork_java.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum VisibilidadPerfil implements IValorDbEnum {
    PUBLICO("publico"),
    PRIVADO("privado");

    private final String valorDb;

    public static VisibilidadPerfil getVisibilidadPerfil(String valorDb) {
        for (VisibilidadPerfil visibilidadPerfil : VisibilidadPerfil.values()) {
            if (visibilidadPerfil.getValorDb().equalsIgnoreCase(valorDb)) {
                return visibilidadPerfil;
            }
        }
        return null;
    }
}
