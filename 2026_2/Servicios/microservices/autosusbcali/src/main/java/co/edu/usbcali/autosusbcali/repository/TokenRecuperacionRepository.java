package co.edu.usbcali.autosusbcali.repository;

import co.edu.usbcali.autosusbcali.domain.TokenRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacion, Long> {
}
