package co.edu.usbcali.dnetwork_java.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Se lanza cuando los datos que llegan en la petición no son válidos
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidacionException extends RuntimeException {
    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
