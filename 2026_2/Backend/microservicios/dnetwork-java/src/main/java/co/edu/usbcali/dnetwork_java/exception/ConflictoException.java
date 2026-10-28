package co.edu.usbcali.dnetwork_java.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Se lanza cuando la operación entra en conflicto con el estado actual de los datos (duplicados, dependencias)
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictoException extends RuntimeException {
    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
