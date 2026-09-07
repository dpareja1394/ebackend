package co.edu.usbcali.dnetwork_java.domain;

import co.edu.usbcali.dnetwork_java.domain.enums.TipoNotificacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "tipo", nullable = false)
    private TipoNotificacion tipo;

    @ManyToOne
    @JoinColumn(name = "origen_usuario_id", nullable = true)
    private Usuario origenUsuario;

    @ManyToOne
    @JoinColumn(name = "publicacion_id", nullable = true)
    private Publicacion publicacion;

    @ManyToOne
    @JoinColumn(name = "comentario_id", nullable = true)
    private Comentario comentario;

    @Column(name = "leida", nullable = false)
    private Boolean leida;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
