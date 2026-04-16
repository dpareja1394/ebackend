package co.edu.usbcali.cinemapareja.repository;

import co.edu.usbcali.cinemapareja.model.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScreeningRepository extends JpaRepository<Screening, Long> {
}
