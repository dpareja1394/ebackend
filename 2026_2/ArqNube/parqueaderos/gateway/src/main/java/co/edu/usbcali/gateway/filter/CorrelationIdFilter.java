package co.edu.usbcali.gateway.filter;

import static net.logstash.logback.argument.StructuredArguments.kv;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Asigna el X-Correlation-Id (lo genera si no viene), lo reenvía al servicio destino, lo devuelve en la
 * respuesta y escribe el log de acceso. Es un WebFilter y no un GlobalFilter para cubrir también las
 * peticiones sin ruta (p. ej. /internal/**) y las respuestas de error.
 */
@Component
public class CorrelationIdFilter implements WebFilter, Ordered {

    public static final String HEADER = "X-Correlation-Id";
    public static final String ATTRIBUTE = CorrelationIdFilter.class.getName() + ".correlationId";

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (path.startsWith("/actuator")) {
            return chain.filter(exchange);
        }

        String recibido = exchange.getRequest().getHeaders().getFirst(HEADER);
        String correlationId = StringUtils.hasText(recibido) ? recibido : UUID.randomUUID().toString();
        long inicio = System.nanoTime();

        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(h -> h.set(HEADER, correlationId))
                .build();
        ServerWebExchange mutado = exchange.mutate().request(request).build();
        mutado.getAttributes().put(ATTRIBUTE, correlationId);
        // Se fija justo antes de enviar la respuesta para reemplazar el que copia el proxy desde el
        // servicio destino; si no, la respuesta llevaría el encabezado dos veces.
        mutado.getResponse().beforeCommit(() -> {
            mutado.getResponse().getHeaders().set(HEADER, correlationId);
            return Mono.empty();
        });

        return chain.filter(mutado).doFinally(signal -> {
            Route route = mutado.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
            var status = mutado.getResponse().getStatusCode();
            log.info("petición atendida",
                    kv("correlationId", correlationId),
                    kv("method", request.getMethod().name()),
                    kv("path", path),
                    kv("route", route != null ? route.getId() : "ninguna"),
                    kv("status", status != null ? status.value() : 0),
                    kv("durationMs", (System.nanoTime() - inicio) / 1_000_000));
        });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
