package co.edu.usbcali.dnetwork_java.domain;

import co.edu.usbcali.dnetwork_java.domain.enums.VisibilidadPerfil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "perfiles")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Perfil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "nombre", nullable = true, length = 150)
    private String nombre;

    @Column(name = "bio", nullable = true, length = 500)
    private String bio;

    @Column(name = "avatar_url", nullable = true, length = 500)
    private String avatarUrl;

    @Column(name = "ubicacion", nullable = true, length = 150)
    private String ubicacion;

    @Column(name = "enlace", nullable = true, length = 255)
    private String enlace;

    @Column(name = "visibilidad", nullable = false)
    private VisibilidadPerfil visibilidad;

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
