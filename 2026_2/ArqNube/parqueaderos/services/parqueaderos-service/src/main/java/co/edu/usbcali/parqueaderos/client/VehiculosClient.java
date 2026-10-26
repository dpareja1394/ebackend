package co.edu.usbcali.parqueaderos.client;

import co.edu.usbcali.parqueaderos.config.CorrelationIdFilter;
import co.edu.usbcali.parqueaderos.exception.ServicioNoDisponibleException;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Cliente de la API interna de vehiculos-service, con timeout y fallo controlado (research.md R3). */
@Component
public class VehiculosClient {

    private static final Logger log = LoggerFactory.getLogger(VehiculosClient.class);
    private static final String NO_DISPONIBLE = "El servicio de vehículos no está disponible";

    private final RestClient restClient;

    public VehiculosClient(@Value("${vehiculos.url}") String baseUrl,
            @Value("${vehiculos.timeout}") Duration timeout) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.defaults()
                .withConnectTimeout(timeout)
                .withReadTimeout(timeout);
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(ClientHttpRequestFactoryBuilder.detect().build(settings))
                .requestInitializer(request -> {
                    String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
                    if (correlationId != null) {
                        request.getHeaders().set(CorrelationIdFilter.HEADER, correlationId);
                    }
                })
                .build();
    }

    public long contarPorParqueadero(UUID parqueaderoId) {
        try {
            ConteoResponse respuesta = restClient.get()
                    .uri("/internal/v1/vehiculos/conteo?parqueaderoId={id}", parqueaderoId)
                    .retrieve()
                    .body(ConteoResponse.class);
            if (respuesta == null) {
                throw new ServicioNoDisponibleException(NO_DISPONIBLE);
            }
            return respuesta.cantidad();
        } catch (RestClientException ex) {
            log.warn("Fallo al contar vehículos del parqueadero {}", parqueaderoId, ex);
            throw new ServicioNoDisponibleException(NO_DISPONIBLE);
        }
    }

    /** Referencia mínima de un vehículo; vacío si el vehículo no existe. */
    public Optional<VehiculoRef> obtenerVehiculo(UUID vehiculoId) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri("/internal/v1/vehiculos/{id}", vehiculoId)
                    .retrieve()
                    .body(VehiculoRef.class));
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                return Optional.empty();
            }
            log.warn("Respuesta inesperada al consultar el vehículo {}", vehiculoId, ex);
            throw new ServicioNoDisponibleException(NO_DISPONIBLE);
        } catch (RestClientException ex) {
            log.warn("Fallo al consultar el vehículo {}", vehiculoId, ex);
            throw new ServicioNoDisponibleException(NO_DISPONIBLE);
        }
    }

    record ConteoResponse(UUID parqueaderoId, long cantidad) {
    }

    public record VehiculoRef(UUID id, String placa, UUID espacioId) {
    }
}
