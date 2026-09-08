package co.edu.usbcali.autosusbcali.repository;

import co.edu.usbcali.autosusbcali.domain.FichaTecnica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FichaTecnicaRepository extends JpaRepository<FichaTecnica, Long> {
}
