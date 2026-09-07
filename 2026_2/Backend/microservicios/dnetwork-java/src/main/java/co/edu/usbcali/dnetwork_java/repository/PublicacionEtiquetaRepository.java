package co.edu.usbcali.dnetwork_java.repository;

import co.edu.usbcali.dnetwork_java.domain.PublicacionEtiqueta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicacionEtiquetaRepository extends JpaRepository<PublicacionEtiqueta, Integer> {
}
