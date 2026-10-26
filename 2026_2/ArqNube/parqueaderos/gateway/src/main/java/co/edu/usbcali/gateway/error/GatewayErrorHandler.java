package co.edu.usbcali.gateway.error;

import co.edu.usbcali.gateway.filter.CorrelationIdFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/** Responde los errores propios del gateway con el formato de error estándar (research.md R9). */
@Component
@Order(-2)
public class GatewayErrorHandler implements ErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GatewayErrorHandler.class);

    private final ObjectMapper objectMapper;

    public GatewayErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        HttpStatusCode status;
        String codigo;
        String mensaje;
        if (tieneCausa(ex, TimeoutException.class) || esStatus(ex, HttpStatus.GATEWAY_TIMEOUT)) {
            status = HttpStatus.GATEWAY_TIMEOUT;
            codigo = "SERVICIO_NO_DISPONIBLE";
            mensaje = "El servicio no respondió a tiempo";
        } else if (tieneCausa(ex, ConnectException.class) || esStatus(ex, HttpStatus.SERVICE_UNAVAILABLE)) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            codigo = "SERVICIO_NO_DISPONIBLE";
            mensaje = "El servicio no está disponible";
        } else if (esStatus(ex, HttpStatus.NOT_FOUND)) {
            status = HttpStatus.NOT_FOUND;
            codigo = "NO_ENCONTRADO";
            mensaje = "Recurso no encontrado";
        } else if (ex instanceof ResponseStatusException rse && rse.getStatusCode().is4xxClientError()) {
            status = rse.getStatusCode();
            codigo = "VALIDACION";
            mensaje = rse.getReason() != null ? rse.getReason() : "Petición inválida";
        } else {
            log.error("Error no controlado en el gateway", ex);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            codigo = "ERROR_INTERNO";
            mensaje = "Ocurrió un error inesperado";
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("codigo", codigo);
        body.put("mensaje", mensaje);
        body.put("detalles", List.of());
        body.put("correlationId", exchange.getAttribute(CorrelationIdFilter.ATTRIBUTE));
        body.put("timestamp", OffsetDateTime.now().toString());

        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (Exception e) {
            bytes = ("{\"codigo\":\"" + codigo + "\"}").getBytes(StandardCharsets.UTF_8);
        }
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    private static boolean esStatus(Throwable ex, HttpStatus status) {
        return ex instanceof ResponseStatusException rse && rse.getStatusCode().value() == status.value();
    }

    private static boolean tieneCausa(Throwable ex, Class<? extends Throwable> tipo) {
        for (Throwable t = ex; t != null; t = t.getCause()) {
            if (tipo.isInstance(t)) {
                return true;
            }
            if (t.getCause() == t) {
                break;
            }
        }
        return false;
    }
}
