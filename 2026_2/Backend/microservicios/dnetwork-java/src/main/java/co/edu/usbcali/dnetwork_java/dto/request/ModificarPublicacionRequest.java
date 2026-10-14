package co.edu.usbcali.dnetwork_java.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ModificarPublicacionRequest(
        String contenido,
        @JsonProperty("privacidad_publicacion") String privacidadPublicacion
) {
}
