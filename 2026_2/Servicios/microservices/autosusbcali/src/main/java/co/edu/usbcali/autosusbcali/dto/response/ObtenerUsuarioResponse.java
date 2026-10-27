package co.edu.usbcali.autosusbcali.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ObtenerUsuarioResponse(
        Long id,
        @JsonProperty("nombre_completo") String nombreCompleto,
        String email, String celular,
        @JsonProperty("tipo_documento") String tipoDocumento,
        @JsonProperty("numero_documento") String numeroDocumento,
        String[] roles
) {
}
