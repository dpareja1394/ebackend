package co.edu.usbcali.dnetwork_java.repository;

import co.edu.usbcali.dnetwork_java.domain.Reaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReaccionRepository extends JpaRepository<Reaccion, Integer> {
}
