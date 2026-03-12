package co.edu.usbcali.cinemapareja.repository;

import co.edu.usbcali.cinemapareja.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {
}
