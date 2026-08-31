package co.edu.usbcali.dnetwork_java.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EstadoDenuncia implements IValorDbEnum {
    PENDIENTE("pendiente"),
    REVISADA("revisada"),
    DESCARTADA("descartada");

    private final String valorDb;

    public static EstadoDenuncia getEstadoDenuncia(String valorDb) {
        for (EstadoDenuncia estadoDenuncia : EstadoDenuncia.values()) {
            if (estadoDenuncia.getValorDb().equalsIgnoreCase(valorDb)) {
                return estadoDenuncia;
            }
        }
        return null;
    }
}
