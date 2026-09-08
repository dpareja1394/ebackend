package co.edu.usbcali.autosusbcali.repository;

import co.edu.usbcali.autosusbcali.domain.SolicitudCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudCreditoRepository extends JpaRepository<SolicitudCredito, Long> {
}
