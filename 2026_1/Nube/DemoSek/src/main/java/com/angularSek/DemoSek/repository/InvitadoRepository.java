package com.angularSek.DemoSek.repository;

import com.angularSek.DemoSek.domain.Invitado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitadoRepository extends JpaRepository<Invitado,Integer> {
}
