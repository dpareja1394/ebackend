package co.edu.usbcali.parqueaderos.exception;

import java.util.List;
import org.springframework.http.HttpStatus;

public class ValidacionException extends NegocioException {

    public ValidacionException(String mensaje) {
        super(HttpStatus.BAD_REQUEST, CodigoError.VALIDACION, mensaje, null);
    }

    public ValidacionException(String mensaje, List<DetalleError> detalles) {
        super(HttpStatus.BAD_REQUEST, CodigoError.VALIDACION, mensaje, detalles);
    }

    public static ValidacionException campo(String campo, String mensaje) {
        return new ValidacionException(mensaje, List.of(new DetalleError(campo, mensaje)));
    }
}
