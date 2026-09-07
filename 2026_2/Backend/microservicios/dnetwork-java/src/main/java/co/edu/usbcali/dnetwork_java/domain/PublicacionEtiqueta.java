package co.edu.usbcali.dnetwork_java.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "publicacion_etiquetas")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublicacionEtiqueta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "publicacion_id", nullable = false)
    private Publicacion publicacion;

    @ManyToOne
    @JoinColumn(name = "etiqueta_id", nullable = false)
    private Etiqueta etiqueta;
}
