package co.edu.usbcali.parqueaderos.exception;

import org.springframework.http.HttpStatus;

public class ServicioNoDisponibleException extends NegocioException {

    public ServicioNoDisponibleException(String mensaje) {
        super(HttpStatus.SERVICE_UNAVAILABLE, CodigoError.SERVICIO_NO_DISPONIBLE, mensaje, null);
    }
}
