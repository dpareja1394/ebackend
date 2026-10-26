package co.edu.usbcali.parqueaderos.exception;

import java.time.OffsetDateTime;
import java.util.List;

/** Formato de error uniforme de todos los servicios (research.md R9). */
public record ErrorResponse(
        CodigoError codigo,
        String mensaje,
        List<DetalleError> detalles,
        String correlationId,
        OffsetDateTime timestamp) {
}
