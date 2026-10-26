package co.edu.usbcali.parqueaderos.service;

import co.edu.usbcali.parqueaderos.client.VehiculosClient;
import co.edu.usbcali.parqueaderos.domain.Espacio;
import co.edu.usbcali.parqueaderos.domain.EstadoEspacio;
import co.edu.usbcali.parqueaderos.dto.EspacioResponse;
import co.edu.usbcali.parqueaderos.dto.OcupacionRequest;
import co.edu.usbcali.parqueaderos.exception.ConflictoException;
import co.edu.usbcali.parqueaderos.exception.ReglaNegocioException;
import co.edu.usbcali.parqueaderos.repository.EspacioRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ocupación y liberación de espacios. Es la única forma de que un espacio pase a OCUPADO o salga de él
 * (FR-007); vehiculos-service la usa al asignar, reasignar y liberar (research.md R2).
 */
@Service
@Transactional
public class OcupacionService {

    private static final Logger log = LoggerFactory.getLogger(OcupacionService.class);
    private static final String NO_DISPONIBLE = "El espacio no está disponible";

    private final EspacioRepository repository;
    private final EspacioService espacioService;
    private final VehiculosClient vehiculosClient;

    public OcupacionService(EspacioRepository repository, EspacioService espacioService,
            VehiculosClient vehiculosClient) {
        this.repository = repository;
        this.espacioService = espacioService;
        this.vehiculosClient = vehiculosClient;
    }

    public EspacioResponse ocupar(UUID espacioId, OcupacionRequest req) {
        Espacio espacio = espacioService.buscar(espacioId);
        if (!espacio.getParqueaderoId().equals(req.parqueaderoId())) {
            throw new ReglaNegocioException("El espacio no pertenece al parqueadero del vehículo");
        }
        if (!espacio.getParqueadero().estaActivo()) {
            throw new ReglaNegocioException("El parqueadero está inactivo");
        }
        if (espacio.getTipoVehiculo() != req.tipoVehiculo()) {
            throw new ReglaNegocioException("El espacio admite " + espacio.getTipoVehiculo()
                    + " y el vehículo es " + req.tipoVehiculo());
        }
        if (espacio.estaOcupado() && req.vehiculoId().equals(espacio.getVehiculoId())) {
            return EspacioResponse.de(espacio);
        }
        if (espacio.getEstado() != EstadoEspacio.DISPONIBLE) {
            throw new ConflictoException(NO_DISPONIBLE);
        }
        if (repository.ocupar(espacioId, req.vehiculoId()) == 0) {
            // Otra petición lo ocupó entre la lectura y la actualización.
            throw new ConflictoException(NO_DISPONIBLE);
        }
        return EspacioResponse.de(espacioService.buscar(espacioId));
    }

    /** Idempotente: liberar un espacio que ya está libre no es un error. */
    public void liberar(UUID espacioId, UUID vehiculoId) {
        Espacio espacio = espacioService.buscar(espacioId);
        if (!espacio.estaOcupado()) {
            return;
        }
        if (!vehiculoId.equals(espacio.getVehiculoId())) {
            throw new ConflictoException("El espacio está ocupado por otro vehículo");
        }
        repository.liberar(espacioId, vehiculoId);
    }

    /**
     * Libera un espacio "huérfano": OCUPADO por un vehículo que ya no existe o que ya no lo tiene asignado,
     * por un fallo parcial entre servicios. Antes de liberar verifica con vehiculos-service.
     */
    public EspacioResponse forzarLiberacion(UUID espacioId) {
        Espacio espacio = espacioService.buscar(espacioId);
        if (!espacio.estaOcupado()) {
            return EspacioResponse.de(espacio);
        }
        UUID vehiculoId = espacio.getVehiculoId();
        var vehiculo = vehiculosClient.obtenerVehiculo(vehiculoId);
        if (vehiculo.isPresent() && espacioId.equals(vehiculo.get().espacioId())) {
            throw new ConflictoException("El espacio está asignado al vehículo " + vehiculo.get().placa()
                    + "; libérelo desde el vehículo");
        }
        repository.liberar(espacioId, vehiculoId);
        log.warn("ESPACIO_HUERFANO_LIBERADO espacioId={} vehiculoId={}", espacioId, vehiculoId);
        return EspacioResponse.de(espacioService.buscar(espacioId));
    }
}
