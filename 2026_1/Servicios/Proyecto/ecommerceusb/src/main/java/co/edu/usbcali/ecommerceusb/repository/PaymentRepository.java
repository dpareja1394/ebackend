package co.edu.usbcali.ecommerceusb.repository;

import co.edu.usbcali.ecommerceusb.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
