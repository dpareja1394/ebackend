package co.edu.usbcali.dnetwork_java.repository;

import co.edu.usbcali.dnetwork_java.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
}
