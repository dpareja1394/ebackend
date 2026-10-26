package co.edu.usbcali.parqueaderos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import co.edu.usbcali.parqueaderos.client.VehiculosClient;
import co.edu.usbcali.parqueaderos.domain.EstadoEspacio;
import co.edu.usbcali.parqueaderos.domain.TipoVehiculo;
import co.edu.usbcali.parqueaderos.dto.EspacioRequest;
import co.edu.usbcali.parqueaderos.dto.OcupacionRequest;
import co.edu.usbcali.parqueaderos.dto.ParqueaderoRequest;
import co.edu.usbcali.parqueaderos.exception.ConflictoException;
import co.edu.usbcali.parqueaderos.exception.ReglaNegocioException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Ocupación contra PostgreSQL real (Testcontainers). Se omite si no hay Docker disponible. */
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class OcupacionServiceIT {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine");

    @MockitoBean
    private VehiculosClient vehiculosClient;

    @Autowired
    private ParqueaderoService parqueaderoService;
    @Autowired
    private EspacioService espacioService;
    @Autowired
    private OcupacionService ocupacionService;

    private UUID parqueaderoId;
    private UUID espacioId;

    @BeforeEach
    void setUp() {
        parqueaderoId = parqueaderoService.crear(new ParqueaderoRequest(
                "Sede " + UUID.randomUUID(), "Cra 4 # 12-30", "Cali", true, null, null)).id();
        espacioId = espacioService.crear(parqueaderoId, new EspacioRequest("A-01", TipoVehiculo.AUTOMOVIL)).id();
    }

    @Test
    void dosVehiculosCompitiendoPorElMismoEspacioSoloUnoLoObtiene() throws Exception {
        var listos = new CountDownLatch(1);
        var pool = Executors.newFixedThreadPool(2);
        List<Future<?>> resultados = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            var req = new OcupacionRequest(UUID.randomUUID(), parqueaderoId, TipoVehiculo.AUTOMOVIL);
            Callable<Object> tarea = () -> {
                listos.await();
                return ocupacionService.ocupar(espacioId, req);
            };
            resultados.add(pool.submit(tarea));
        }
        listos.countDown();

        int exitos = 0;
        int conflictos = 0;
        for (Future<?> f : resultados) {
            try {
                f.get();
                exitos++;
            } catch (ExecutionException ex) {
                assertThat(ex.getCause()).isInstanceOf(ConflictoException.class);
                conflictos++;
            }
        }
        pool.shutdown();

        assertThat(exitos).isEqualTo(1);
        assertThat(conflictos).isEqualTo(1);
        assertThat(espacioService.obtener(espacioId).estado()).isEqualTo(EstadoEspacio.OCUPADO);
    }

    @Test
    void tipoIncompatibleDevuelveReglaDeNegocio() {
        var req = new OcupacionRequest(UUID.randomUUID(), parqueaderoId, TipoVehiculo.MOTOCICLETA);

        assertThatThrownBy(() -> ocupacionService.ocupar(espacioId, req))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessage("El espacio admite AUTOMOVIL y el vehículo es MOTOCICLETA");
    }

    @Test
    void liberarEsIdempotente() {
        UUID vehiculoId = UUID.randomUUID();
        ocupacionService.ocupar(espacioId, new OcupacionRequest(vehiculoId, parqueaderoId, TipoVehiculo.AUTOMOVIL));

        ocupacionService.liberar(espacioId, vehiculoId);
        ocupacionService.liberar(espacioId, vehiculoId);

        var espacio = espacioService.obtener(espacioId);
        assertThat(espacio.estado()).isEqualTo(EstadoEspacio.DISPONIBLE);
        assertThat(espacio.vehiculoId()).isNull();
    }
}
