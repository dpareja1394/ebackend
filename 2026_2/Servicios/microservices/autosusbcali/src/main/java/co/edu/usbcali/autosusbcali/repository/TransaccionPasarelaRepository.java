package co.edu.usbcali.autosusbcali.repository;

import co.edu.usbcali.autosusbcali.domain.TransaccionPasarela;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransaccionPasarelaRepository extends JpaRepository<TransaccionPasarela, Long> {
}
