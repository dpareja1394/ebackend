package co.edu.usbcali.cinema.repository;

import co.edu.usbcali.cinema.domain.PurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseStatusRepository extends JpaRepository<PurchaseStatus, Integer> {
}
