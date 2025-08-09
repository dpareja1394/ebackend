import co.edu.usbcali.aerolineaplus.domain.Ciudad;
import co.edu.usbcali.aerolineaplus.domain.Pais;
import co.edu.usbcali.aerolineaplus.repository.CiudadRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class CiudadRepositoryTest {

    @Autowired
    private CiudadRepository ciudadRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testExistsByPaisId() {
        //Crea un país y una ciudad relacionados
        Pais pais = Pais.builder()
                .nombre("Colombia")
                .build();
        entityManager.persist(pais);

        Ciudad ciudad = Ciudad.builder()
                .nombre("Bogotá")
                .descripcion("Capital")
                .pais(pais)
                .build();
        entityManager.persist(ciudad);

        //Llama al método que se está probando
        Boolean exists = ciudadRepository.existsByPaisId(pais.getId());

        //Verifica que el resultado sea el esperado
        Assertions.assertTrue(exists, "Debería existir una ciudad asociada al país");
    }
}
