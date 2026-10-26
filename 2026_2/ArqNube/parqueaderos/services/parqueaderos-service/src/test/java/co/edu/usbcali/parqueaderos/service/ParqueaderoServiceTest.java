package co.edu.usbcali.parqueaderos.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.usbcali.parqueaderos.client.VehiculosClient;
import co.edu.usbcali.parqueaderos.domain.Parqueadero;
import co.edu.usbcali.parqueaderos.dto.ParqueaderoRequest;
import co.edu.usbcali.parqueaderos.exception.CodigoError;
import co.edu.usbcali.parqueaderos.exception.ConflictoException;
import co.edu.usbcali.parqueaderos.exception.ServicioNoDisponibleException;
import co.edu.usbcali.parqueaderos.repository.EspacioRepository;
import co.edu.usbcali.parqueaderos.repository.ParqueaderoRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ParqueaderoServiceTest {

    @Mock
    private ParqueaderoRepository repository;
    @Mock
    private EspacioRepository espacioRepository;
    @Mock
    private VehiculosClient vehiculosClient;
    @InjectMocks
    private ParqueaderoService service;

    private final UUID id = UUID.randomUUID();
    private final Parqueadero parqueadero = new Parqueadero();

    @BeforeEach
    void setUp() {
        parqueadero.setNombre("Sede Centro");
        parqueadero.setCiudad("Cali");
    }

    @Test
    void crearConNombreDuplicadoEnLaMismaCiudadDevuelve409() {
        when(repository.existsByNombreIgnoreCaseAndCiudadIgnoreCase("Sede Centro", "Cali")).thenReturn(true);
        var req = new ParqueaderoRequest(" Sede Centro ", "Cra 4 # 12-30", "Cali ", true, null, null);

        assertThatThrownBy(() -> service.crear(req))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("Ya existe un parqueadero llamado 'Sede Centro' en Cali")
                .extracting("status").isEqualTo(HttpStatus.CONFLICT);
        verify(repository, never()).save(any());
    }

    @Test
    void eliminarConEspaciosDevuelve409SinConsultarVehiculos() {
        when(repository.findById(id)).thenReturn(Optional.of(parqueadero));
        when(espacioRepository.countByParqueaderoId(id)).thenReturn(3L);

        assertThatThrownBy(() -> service.eliminar(id))
                .isInstanceOf(ConflictoException.class)
                .hasMessage("El parqueadero tiene 3 espacios; márquelo como INACTIVO");
        verify(vehiculosClient, never()).contarPorParqueadero(any());
        verify(repository, never()).delete(any(Parqueadero.class));
    }

    @Test
    void eliminarConVehiculosDevuelve409() {
        when(repository.findById(id)).thenReturn(Optional.of(parqueadero));
        when(vehiculosClient.contarPorParqueadero(id)).thenReturn(2L);

        assertThatThrownBy(() -> service.eliminar(id))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("2 vehículos asociados");
        verify(repository, never()).delete(any(Parqueadero.class));
    }

    @Test
    void eliminarConServicioDeVehiculosCaidoDevuelve503YNoElimina() {
        when(repository.findById(id)).thenReturn(Optional.of(parqueadero));
        when(vehiculosClient.contarPorParqueadero(id))
                .thenThrow(new ServicioNoDisponibleException("El servicio de vehículos no está disponible"));

        assertThatThrownBy(() -> service.eliminar(id))
                .isInstanceOf(ServicioNoDisponibleException.class)
                .extracting("codigo").isEqualTo(CodigoError.SERVICIO_NO_DISPONIBLE);
        verify(repository, never()).delete(any(Parqueadero.class));
    }

    @Test
    void eliminarSinEspaciosNiVehiculosBorra() {
        when(repository.findById(id)).thenReturn(Optional.of(parqueadero));

        service.eliminar(id);

        verify(repository).delete(parqueadero);
    }
}
