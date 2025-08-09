package co.edu.usbcali.aerolineaplus.repository;

import co.edu.usbcali.aerolineaplus.domain.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Integer>{

    Optional<Pais> findByCodigo(String codigo);

}
