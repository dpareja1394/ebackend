package co.edu.usbcali.autosusbcali.repository;

import co.edu.usbcali.autosusbcali.domain.AccesorioCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccesorioCotizacionRepository extends JpaRepository<AccesorioCotizacion, Long> {
}
