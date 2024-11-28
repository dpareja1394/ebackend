import co.edu.usbcali.aerolineaplus.domain.Ciudad;
import co.edu.usbcali.aerolineaplus.domain.Pais;
import co.edu.usbcali.aerolineaplus.dto.response.ListarCiudadesResponse;
import co.edu.usbcali.aerolineaplus.repository.CiudadRepository;
import co.edu.usbcali.aerolineaplus.service.implementation.CiudadServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CiudadServiceTest {

    @Mock
    private CiudadRepository ciudadRepository;

    @InjectMocks
    private CiudadServiceImpl ciudadService;

    @Test
    public void testObtenerCiudades() {
        Pais paisMock = Pais.builder()
                .id(1)
                .nombre("Colombia")
                .build();

        List<Ciudad> ciudadesMock = List.of(
                Ciudad.builder()
                        .id(1)
                        .nombre("Bogotá")
                        .descripcion("Capital")
                        .pais(paisMock)
                        .build()
        );

        Mockito.when(ciudadRepository.findAll()).thenReturn(ciudadesMock);

        List<ListarCiudadesResponse> response = ciudadService.obtenerCiudades();

        Assertions.assertEquals(1, response.size(), "Debería haber una ciudad en la lista");
        Assertions.assertEquals("Bogotá", response.get(0).getNombre());
        Assertions.assertEquals("Colombia", response.get(0).getNombrePais());
    }
}
