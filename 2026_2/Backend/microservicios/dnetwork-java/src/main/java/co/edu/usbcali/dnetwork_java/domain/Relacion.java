package co.edu.usbcali.dnetwork_java.domain;

import co.edu.usbcali.dnetwork_java.domain.enums.EstadoRelacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "relaciones")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Relacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "seguidor_id", nullable = false)
    private Usuario seguidor;

    @ManyToOne
    @JoinColumn(name = "seguido_id", nullable = false)
    private Usuario seguido;

    @Column(name = "estado", nullable = false)
    private EstadoRelacion estado;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
