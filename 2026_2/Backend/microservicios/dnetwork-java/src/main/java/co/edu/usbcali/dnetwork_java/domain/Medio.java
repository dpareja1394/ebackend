package co.edu.usbcali.dnetwork_java.domain;

import co.edu.usbcali.dnetwork_java.domain.enums.TipoMedio;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "medios")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Medio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "publicacion_id", nullable = false)
    private Publicacion publicacion;

    @Column(name = "tipo", nullable = false)
    private TipoMedio tipo;

    @Column(name = "url_original", nullable = false, length = 500)
    private String urlOriginal;

    @Column(name = "url_thumbnail", nullable = true, length = 500)
    private String urlThumbnail;

    @Column(name = "formato", nullable = true, length = 20)
    private String formato;

    @Column(name = "tamano_bytes", nullable = true)
    private Integer tamanioBytes;

    @Column(name = "orden", nullable = false)
    private Short orden;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
