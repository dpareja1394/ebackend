package co.edu.usbcali.parqueaderos.service;

import co.edu.usbcali.parqueaderos.client.VehiculosClient;
import co.edu.usbcali.parqueaderos.domain.EstadoParqueadero;
import co.edu.usbcali.parqueaderos.domain.Parqueadero;
import co.edu.usbcali.parqueaderos.dto.PaginaResponse;
import co.edu.usbcali.parqueaderos.dto.Paginacion;
import co.edu.usbcali.parqueaderos.dto.ParqueaderoRequest;
import co.edu.usbcali.parqueaderos.dto.ParqueaderoResponse;
import co.edu.usbcali.parqueaderos.dto.ParqueaderoUpdateRequest;
import co.edu.usbcali.parqueaderos.exception.ConflictoException;
import co.edu.usbcali.parqueaderos.exception.RecursoNoEncontradoException;
import co.edu.usbcali.parqueaderos.repository.EspacioRepository;
import co.edu.usbcali.parqueaderos.repository.ParqueaderoRepository;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** Reglas de negocio de parqueaderos (FR-001 a FR-004). Todas las validaciones ocurren antes de guardar. */
@Service
@Transactional
public class ParqueaderoService {

    private final ParqueaderoRepository repository;
    private final EspacioRepository espacioRepository;
    private final VehiculosClient vehiculosClient;

    public ParqueaderoService(ParqueaderoRepository repository, EspacioRepository espacioRepository,
            VehiculosClient vehiculosClient) {
        this.repository = repository;
        this.espacioRepository = espacioRepository;
        this.vehiculosClient = vehiculosClient;
    }

    public ParqueaderoResponse crear(ParqueaderoRequest req) {
        String nombre = req.nombre().trim();
        String ciudad = req.ciudad().trim();
        if (repository.existsByNombreIgnoreCaseAndCiudadIgnoreCase(nombre, ciudad)) {
            throw duplicado(nombre, ciudad);
        }
        Parqueadero p = new Parqueadero();
        p.setNombre(nombre);
        p.setDireccion(req.direccion().trim());
        p.setCiudad(ciudad);
        p.definirHorario(req.abierto24h(), req.horaApertura(), req.horaCierre());
        p.setEstado(EstadoParqueadero.ACTIVO);
        return aRespuesta(repository.save(p));
    }

    @Transactional(readOnly = true)
    public ParqueaderoResponse obtener(UUID id) {
        return aRespuesta(buscar(id));
    }

    @Transactional(readOnly = true)
    public PaginaResponse<ParqueaderoResponse> listar(String ciudad, EstadoParqueadero estado, Paginacion paginacion) {
        Specification<Parqueadero> spec = (root, query, cb) -> cb.conjunction();
        if (StringUtils.hasText(ciudad)) {
            String valor = ciudad.trim().toLowerCase();
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("ciudad")), valor));
        }
        if (estado != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("estado"), estado));
        }
        var pagina = repository.findAll(spec, paginacion.toPageRequest(Sort.by("ciudad", "nombre")));
        return PaginaResponse.from(pagina.map(this::aRespuesta));
    }

    public ParqueaderoResponse actualizar(UUID id, ParqueaderoUpdateRequest req) {
        Parqueadero p = buscar(id);
        String nombre = req.nombre().trim();
        String ciudad = req.ciudad().trim();
        if (repository.existsByNombreIgnoreCaseAndCiudadIgnoreCaseAndIdNot(nombre, ciudad, id)) {
            throw duplicado(nombre, ciudad);
        }
        p.setNombre(nombre);
        p.setDireccion(req.direccion().trim());
        p.setCiudad(ciudad);
        p.definirHorario(req.abierto24h(), req.horaApertura(), req.horaCierre());
        p.setEstado(req.estado());
        return aRespuesta(repository.saveAndFlush(p));
    }

    public void eliminar(UUID id) {
        Parqueadero p = buscar(id);
        long espacios = espacioRepository.countByParqueaderoId(id);
        if (espacios > 0) {
            throw new ConflictoException("El parqueadero tiene " + espacios + " espacios; márquelo como INACTIVO");
        }
        long vehiculos = vehiculosClient.contarPorParqueadero(id);
        if (vehiculos > 0) {
            throw new ConflictoException("El parqueadero tiene " + vehiculos
                    + " vehículos asociados; márquelo como INACTIVO");
        }
        repository.delete(p);
    }

    Parqueadero buscar(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El parqueadero " + id + " no existe"));
    }

    private ParqueaderoResponse aRespuesta(Parqueadero p) {
        return ParqueaderoResponse.de(p, espacioRepository.countByParqueaderoId(p.getId()));
    }

    private static ConflictoException duplicado(String nombre, String ciudad) {
        return new ConflictoException("Ya existe un parqueadero llamado '" + nombre + "' en " + ciudad);
    }
}
