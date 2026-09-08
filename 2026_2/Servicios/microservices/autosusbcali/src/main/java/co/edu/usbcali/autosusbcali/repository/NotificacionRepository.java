package co.edu.usbcali.autosusbcali.repository;

import co.edu.usbcali.autosusbcali.domain.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
}
