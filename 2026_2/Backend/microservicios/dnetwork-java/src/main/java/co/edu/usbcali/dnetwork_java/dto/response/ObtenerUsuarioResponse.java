package co.edu.usbcali.dnetwork_java.dto.response;

import java.time.LocalDateTime;

public record ObtenerUsuarioResponse(
        Integer id,
        String email,
        String username,
        LocalDateTime lastLogin
) {
}
