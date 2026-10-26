package co.edu.usbcali.parqueaderos.exception;

import java.util.List;
import org.springframework.http.HttpStatus;

/** Base de las excepciones de negocio: cada subclase define su estado HTTP y código. */
public abstract class NegocioException extends RuntimeException {

    private final HttpStatus status;
    private final CodigoError codigo;
    private final List<DetalleError> detalles;

    protected NegocioException(HttpStatus status, CodigoError codigo, String mensaje, List<DetalleError> detalles) {
        super(mensaje);
        this.status = status;
        this.codigo = codigo;
        this.detalles = detalles == null ? List.of() : List.copyOf(detalles);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public CodigoError getCodigo() {
        return codigo;
    }

    public List<DetalleError> getDetalles() {
        return detalles;
    }
}
