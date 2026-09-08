package co.edu.usbcali.autosusbcali.repository;

import co.edu.usbcali.autosusbcali.domain.ImagenVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImagenVehiculoRepository extends JpaRepository<ImagenVehiculo, Long> {
}
