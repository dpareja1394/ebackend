package co.edu.usbcali.aerolineaplus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.usbcali.aerolineaplus.domain.Aeropuerto;

@Repository
public interface AeropuertoRepository extends JpaRepository<Aeropuerto, Integer> {

    Boolean existsByCiudadId(Integer ciudadId);
    
}
