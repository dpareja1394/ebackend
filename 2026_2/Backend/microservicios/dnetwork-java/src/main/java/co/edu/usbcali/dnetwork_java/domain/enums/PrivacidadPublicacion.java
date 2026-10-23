package co.edu.usbcali.dnetwork_java.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum PrivacidadPublicacion implements IValorDbEnum {
    PUBLICO("publico"),
    SEGUIDORES("seguidores"),
    PRIVADO("privado");

    private final String valorDb;

    public static PrivacidadPublicacion getPrivacidadPublicacion(String valorDb) {
        return Arrays.stream(values())
                .filter(p -> p.valorDb.equalsIgnoreCase(valorDb))
                .findFirst()
                .orElse(null);
    }

}
