package co.edu.usbcali.parqueaderos.exception;

import org.springframework.http.HttpStatus;

public class ConflictoException extends NegocioException {

    public ConflictoException(String mensaje) {
        super(HttpStatus.CONFLICT, CodigoError.CONFLICTO, mensaje, null);
    }
}
