package co.edu.usbcali.autosusbcali.repository;

import co.edu.usbcali.autosusbcali.domain.VerificacionRunt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificacionRuntRepository extends JpaRepository<VerificacionRunt, Long> {
}
