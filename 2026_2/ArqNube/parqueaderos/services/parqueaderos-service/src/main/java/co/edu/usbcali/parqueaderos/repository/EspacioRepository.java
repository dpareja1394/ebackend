package co.edu.usbcali.parqueaderos.repository;

import co.edu.usbcali.parqueaderos.domain.Espacio;
import co.edu.usbcali.parqueaderos.domain.EstadoEspacio;
import co.edu.usbcali.parqueaderos.domain.TipoVehiculo;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EspacioRepository extends JpaRepository<Espacio, UUID>, JpaSpecificationExecutor<Espacio> {

    boolean existsByParqueaderoIdAndCodigo(UUID parqueaderoId, String codigo);

    boolean existsByParqueaderoIdAndCodigoAndIdNot(UUID parqueaderoId, String codigo, UUID id);

    long countByParqueaderoId(UUID parqueaderoId);

    /** Cantidad de espacios por tipo y estado; el resumen se calcula siempre a partir de esto (SC-005). */
    @Query("""
            select e.tipoVehiculo as tipoVehiculo, e.estado as estado, count(e) as cantidad
            from Espacio e
            where e.parqueaderoId = :parqueaderoId
            group by e.tipoVehiculo, e.estado
            """)
    List<ConteoPorTipoEstado> contarPorTipoYEstado(@Param("parqueaderoId") UUID parqueaderoId);

    /**
     * Ocupa el espacio solo si sigue DISPONIBLE. Es atómico: si dos peticiones compiten por el mismo
     * espacio, solo una actualiza la fila (devuelve 1) y la otra recibe 0.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Espacio e
            set e.estado = co.edu.usbcali.parqueaderos.domain.EstadoEspacio.OCUPADO,
                e.vehiculoId = :vehiculoId,
                e.actualizadoEn = CURRENT_TIMESTAMP
            where e.id = :id and e.estado = co.edu.usbcali.parqueaderos.domain.EstadoEspacio.DISPONIBLE
            """)
    int ocupar(@Param("id") UUID id, @Param("vehiculoId") UUID vehiculoId);

    /** Libera el espacio solo si lo ocupa ese vehículo. */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Espacio e
            set e.estado = co.edu.usbcali.parqueaderos.domain.EstadoEspacio.DISPONIBLE,
                e.vehiculoId = null,
                e.actualizadoEn = CURRENT_TIMESTAMP
            where e.id = :id and e.vehiculoId = :vehiculoId
            """)
    int liberar(@Param("id") UUID id, @Param("vehiculoId") UUID vehiculoId);

    interface ConteoPorTipoEstado {
        TipoVehiculo getTipoVehiculo();

        EstadoEspacio getEstado();

        long getCantidad();
    }
}
