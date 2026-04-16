package co.edu.usbcali.cinemapareja.repository;

import co.edu.usbcali.cinemapareja.model.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheaterRespository extends JpaRepository<Theater, Integer> {
}
