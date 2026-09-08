package co.edu.usbcali.autosusbcali.repository;

import co.edu.usbcali.autosusbcali.domain.DocumentoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoVehiculoRepository extends JpaRepository<DocumentoVehiculo, Long> {
}
