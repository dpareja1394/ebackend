package co.edu.usbcali.parqueaderos.exception;

import co.edu.usbcali.parqueaderos.config.CorrelationIdFilter;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/** Traduce todas las excepciones al formato de error estándar (FR-019, research.md R9). */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<ErrorResponse> negocio(NegocioException ex) {
        return responder(ex.getStatus(), ex.getCodigo(), ex.getMessage(), ex.getDetalles());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException ex) {
        List<DetalleError> detalles = ex.getBindingResult().getAllErrors().stream()
                .map(error -> new DetalleError(
                        error instanceof FieldError fe ? fe.getField() : error.getObjectName(),
                        error.getDefaultMessage()))
                .toList();
        return responder(HttpStatus.BAD_REQUEST, CodigoError.VALIDACION, "Datos inválidos", detalles);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> validacionParametros(HandlerMethodValidationException ex) {
        List<DetalleError> detalles = ex.getParameterValidationResults().stream()
                .flatMap(r -> r.getResolvableErrors().stream()
                        .map(e -> new DetalleError(r.getMethodParameter().getParameterName(), e.getDefaultMessage())))
                .toList();
        return responder(HttpStatus.BAD_REQUEST, CodigoError.VALIDACION, "Parámetros inválidos", detalles);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> cuerpoIlegible(HttpMessageNotReadableException ex) {
        List<DetalleError> detalles = List.of();
        if (ex.getCause() instanceof MismatchedInputException mie && !mie.getPath().isEmpty()) {
            String campo = mie.getPath().stream()
                    .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : "[" + ref.getIndex() + "]")
                    .collect(Collectors.joining("."));
            String mensaje = mie instanceof InvalidFormatException ife
                    ? "Valor no válido: '" + ife.getValue() + "'"
                    : "Valor no válido";
            detalles = List.of(new DetalleError(campo, mensaje));
        }
        return responder(HttpStatus.BAD_REQUEST, CodigoError.VALIDACION, "El cuerpo de la petición no es válido", detalles);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> tipoParametro(MethodArgumentTypeMismatchException ex) {
        String mensaje = "Valor no válido: '" + ex.getValue() + "'";
        return responder(HttpStatus.BAD_REQUEST, CodigoError.VALIDACION, "Parámetros inválidos",
                List.of(new DetalleError(ex.getName(), mensaje)));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> parametroFaltante(MissingServletRequestParameterException ex) {
        return responder(HttpStatus.BAD_REQUEST, CodigoError.VALIDACION, "Parámetros inválidos",
                List.of(new DetalleError(ex.getParameterName(), "Es obligatorio")));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> integridad(DataIntegrityViolationException ex) {
        log.warn("Violación de integridad de datos", ex);
        return responder(HttpStatus.CONFLICT, CodigoError.CONFLICTO,
                "La operación entra en conflicto con datos existentes", List.of());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> rutaInexistente(NoResourceFoundException ex) {
        return responder(HttpStatus.NOT_FOUND, CodigoError.NO_ENCONTRADO, "Recurso no encontrado", List.of());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> metodoNoSoportado(HttpRequestMethodNotSupportedException ex) {
        return responder(HttpStatus.METHOD_NOT_ALLOWED, CodigoError.VALIDACION,
                "Método " + ex.getMethod() + " no permitido en esta ruta", List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> inesperado(Exception ex) {
        log.error("Error no controlado", ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, CodigoError.ERROR_INTERNO,
                "Ocurrió un error inesperado", List.of());
    }

    private ResponseEntity<ErrorResponse> responder(HttpStatus status, CodigoError codigo, String mensaje,
            List<DetalleError> detalles) {
        ErrorResponse body = new ErrorResponse(codigo, mensaje, detalles,
                MDC.get(CorrelationIdFilter.MDC_KEY), OffsetDateTime.now());
        return ResponseEntity.status(status).body(body);
    }
}
