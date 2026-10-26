package co.edu.usbcali.parqueaderos.repository;

import co.edu.usbcali.parqueaderos.domain.Parqueadero;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ParqueaderoRepository extends JpaRepository<Parqueadero, UUID>, JpaSpecificationExecutor<Parqueadero> {

    boolean existsByNombreIgnoreCaseAndCiudadIgnoreCase(String nombre, String ciudad);

    boolean existsByNombreIgnoreCaseAndCiudadIgnoreCaseAndIdNot(String nombre, String ciudad, UUID id);
}
