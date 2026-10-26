package co.edu.usbcali.parqueaderos.service;

import co.edu.usbcali.parqueaderos.domain.Espacio;
import co.edu.usbcali.parqueaderos.domain.EstadoEspacio;
import co.edu.usbcali.parqueaderos.domain.Parqueadero;
import co.edu.usbcali.parqueaderos.domain.TipoVehiculo;
import co.edu.usbcali.parqueaderos.dto.ConteoEspacios;
import co.edu.usbcali.parqueaderos.dto.EspacioRequest;
import co.edu.usbcali.parqueaderos.dto.EspacioResponse;
import co.edu.usbcali.parqueaderos.dto.EspacioUpdateRequest;
import co.edu.usbcali.parqueaderos.dto.PaginaResponse;
import co.edu.usbcali.parqueaderos.dto.Paginacion;
import co.edu.usbcali.parqueaderos.dto.ResumenResponse;
import co.edu.usbcali.parqueaderos.exception.ConflictoException;
import co.edu.usbcali.parqueaderos.exception.RecursoNoEncontradoException;
import co.edu.usbcali.parqueaderos.repository.EspacioRepository;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Reglas de negocio de espacios (FR-005 a FR-009). El estado OCUPADO solo lo cambia OcupacionService. */
@Service
@Transactional
public class EspacioService {

    private final EspacioRepository repository;
    private final ParqueaderoService parqueaderoService;

    public EspacioService(EspacioRepository repository, ParqueaderoService parqueaderoService) {
        this.repository = repository;
        this.parqueaderoService = parqueaderoService;
    }

    public EspacioResponse crear(UUID parqueaderoId, EspacioRequest req) {
        Parqueadero parqueadero = parqueaderoService.buscar(parqueaderoId);
        String codigo = Espacio.normalizarCodigo(req.codigo());
        if (repository.existsByParqueaderoIdAndCodigo(parqueaderoId, codigo)) {
            throw duplicado(codigo);
        }
        Espacio espacio = new Espacio(parqueadero, codigo, req.tipoVehiculo());
        return EspacioResponse.de(repository.save(espacio));
    }

    @Transactional(readOnly = true)
    public EspacioResponse obtener(UUID id) {
        return EspacioResponse.de(buscar(id));
    }

    @Transactional(readOnly = true)
    public PaginaResponse<EspacioResponse> listar(UUID parqueaderoId, EstadoEspacio estado, TipoVehiculo tipo,
            Paginacion paginacion) {
        parqueaderoService.buscar(parqueaderoId);
        Specification<Espacio> spec = (root, query, cb) -> cb.equal(root.get("parqueaderoId"), parqueaderoId);
        if (estado != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("estado"), estado));
        }
        if (tipo != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tipoVehiculo"), tipo));
        }
        var pagina = repository.findAll(spec, paginacion.toPageRequest(Sort.by("codigo")));
        return PaginaResponse.from(pagina.map(EspacioResponse::de));
    }

    public EspacioResponse actualizar(UUID id, EspacioUpdateRequest req) {
        Espacio espacio = buscar(id);
        String codigo = Espacio.normalizarCodigo(req.codigo());
        if (espacio.estaOcupado()) {
            boolean cambia = !espacio.getCodigo().equals(codigo)
                    || espacio.getTipoVehiculo() != req.tipoVehiculo()
                    || req.estado() != EstadoEspacio.OCUPADO;
            if (cambia) {
                throw new ConflictoException("El espacio está ocupado por un vehículo");
            }
            return EspacioResponse.de(espacio);
        }
        if (repository.existsByParqueaderoIdAndCodigoAndIdNot(espacio.getParqueaderoId(), codigo, id)) {
            throw duplicado(codigo);
        }
        espacio.setCodigo(codigo);
        espacio.setTipoVehiculo(req.tipoVehiculo());
        espacio.setEstado(req.estado());
        return EspacioResponse.de(repository.saveAndFlush(espacio));
    }

    public void eliminar(UUID id) {
        Espacio espacio = buscar(id);
        if (espacio.estaOcupado()) {
            throw new ConflictoException("No se puede eliminar un espacio ocupado");
        }
        repository.delete(espacio);
    }

    @Transactional(readOnly = true)
    public ResumenResponse resumen(UUID parqueaderoId) {
        parqueaderoService.buscar(parqueaderoId);
        Map<TipoVehiculo, ConteoEspacios> porTipo = new EnumMap<>(TipoVehiculo.class);
        for (TipoVehiculo tipo : TipoVehiculo.values()) {
            porTipo.put(tipo, ConteoEspacios.VACIO);
        }
        for (var fila : repository.contarPorTipoYEstado(parqueaderoId)) {
            long n = fila.getCantidad();
            ConteoEspacios parcial = switch (fila.getEstado()) {
                case DISPONIBLE -> new ConteoEspacios(n, n, 0, 0);
                case OCUPADO -> new ConteoEspacios(n, 0, n, 0);
                case FUERA_DE_SERVICIO -> new ConteoEspacios(n, 0, 0, n);
            };
            porTipo.merge(fila.getTipoVehiculo(), parcial, ConteoEspacios::sumar);
        }
        ConteoEspacios general = porTipo.values().stream().reduce(ConteoEspacios.VACIO, ConteoEspacios::sumar);
        return new ResumenResponse(parqueaderoId, general, porTipo);
    }

    Espacio buscar(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El espacio " + id + " no existe"));
    }

    private static ConflictoException duplicado(String codigo) {
        return new ConflictoException("Ya existe el espacio '" + codigo + "' en este parqueadero");
    }
}
