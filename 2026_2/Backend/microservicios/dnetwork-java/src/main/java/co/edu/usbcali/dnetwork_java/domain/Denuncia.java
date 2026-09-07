package co.edu.usbcali.dnetwork_java.domain;

import co.edu.usbcali.dnetwork_java.domain.enums.EstadoDenuncia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "denuncias")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Denuncia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_reportante_id", nullable = false)
    private Usuario usuarioReportante;

    @ManyToOne
    @JoinColumn(name = "usuario_reportado_id", nullable = true)
    private Usuario usuarioReportado;

    @ManyToOne
    @JoinColumn(name = "publicacion_id", nullable = true)
    private Publicacion publicacion;

    @ManyToOne
    @JoinColumn(name = "comentario_id", nullable = true)
    private Comentario comentario;

    @Column(name = "motivo", nullable = false, length = 255)
    private String motivo;

    @Column(name = "estado", nullable = false)
    private EstadoDenuncia estado;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
