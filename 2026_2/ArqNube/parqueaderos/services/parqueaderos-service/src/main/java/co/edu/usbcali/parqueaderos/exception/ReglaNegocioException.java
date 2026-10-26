package co.edu.usbcali.parqueaderos.exception;

import org.springframework.http.HttpStatus;

public class ReglaNegocioException extends NegocioException {

    public ReglaNegocioException(String mensaje) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, CodigoError.REGLA_NEGOCIO, mensaje, null);
    }
}
