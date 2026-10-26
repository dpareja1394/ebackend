package co.edu.usbcali.parqueaderos.exception;

import org.springframework.http.HttpStatus;

public class RecursoNoEncontradoException extends NegocioException {

    public RecursoNoEncontradoException(String mensaje) {
        super(HttpStatus.NOT_FOUND, CodigoError.NO_ENCONTRADO, mensaje, null);
    }
}
