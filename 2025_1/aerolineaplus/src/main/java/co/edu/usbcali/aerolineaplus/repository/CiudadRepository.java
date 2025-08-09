package co.edu.usbcali.aerolineaplus.repository;

import co.edu.usbcali.aerolineaplus.domain.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CiudadRepository extends JpaRepository<Ciudad, Integer> {

    Boolean existsByPaisId(Integer paisId);

}
