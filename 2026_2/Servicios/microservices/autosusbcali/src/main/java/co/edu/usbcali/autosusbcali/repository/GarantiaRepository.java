package co.edu.usbcali.autosusbcali.repository;

import co.edu.usbcali.autosusbcali.domain.Garantia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GarantiaRepository extends JpaRepository<Garantia, Long> {
}
