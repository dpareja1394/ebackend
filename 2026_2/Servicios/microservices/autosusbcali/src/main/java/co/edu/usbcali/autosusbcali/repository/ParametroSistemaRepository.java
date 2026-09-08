package co.edu.usbcali.autosusbcali.repository;

import co.edu.usbcali.autosusbcali.domain.ParametroSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParametroSistemaRepository extends JpaRepository<ParametroSistema, Long> {
}
