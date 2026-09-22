package co.edu.usbcali.dnetwork_java.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record ObtenerPublicacionResponse(
        Integer id,
        @JsonProperty("autor_username") String autorUsername,
        @JsonProperty("autor_id") Integer autorId,
        String contenido,
        @JsonProperty("privacidad_publicacion") String privacidadPublicacion,
        @JsonProperty("created_at") LocalDateTime createdAt,
        @JsonProperty("updated_at") LocalDateTime updatedAt
        ) {
    // Implementar los atributos en snake_case para json
}
