package co.edu.usbcali.autosusbcali.repository;

import co.edu.usbcali.autosusbcali.domain.EntidadFinanciera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntidadFinancieraRepository extends JpaRepository<EntidadFinanciera, Long> {
}
