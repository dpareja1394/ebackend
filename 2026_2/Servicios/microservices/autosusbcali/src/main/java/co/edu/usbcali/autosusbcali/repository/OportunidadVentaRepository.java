package co.edu.usbcali.autosusbcali.repository;

import co.edu.usbcali.autosusbcali.domain.OportunidadVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OportunidadVentaRepository extends JpaRepository<OportunidadVenta, Long> {
}
