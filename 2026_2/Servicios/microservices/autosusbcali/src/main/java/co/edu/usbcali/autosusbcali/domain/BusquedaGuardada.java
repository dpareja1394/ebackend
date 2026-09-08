package co.edu.usbcali.autosusbcali.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "busquedas_guardadas")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BusquedaGuardada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "nombre", length = 100)
    private String nombre;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "criterios", nullable = false, columnDefinition = "jsonb")
    private String criterios;

    @Column(name = "alerta_activa", nullable = false)
    private Boolean alertaActiva;

    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;
}
