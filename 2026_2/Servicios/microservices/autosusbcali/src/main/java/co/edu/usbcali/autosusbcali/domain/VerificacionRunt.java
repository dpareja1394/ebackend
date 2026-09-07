package co.edu.usbcali.autosusbcali.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "verificaciones_runt")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerificacionRunt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @Column(name = "placa", length = 10, nullable = false)
    private String placa;

    @Column(name = "tiene_prendas")
    private Boolean tienePrendas;

    @Column(name = "tiene_embargos")
    private Boolean tieneEmbargos;

    @Column(name = "reporte_hurto")
    private Boolean reporteHurto;

    @Column(name = "estado_legal", length = 50)
    private String estadoLegal;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "respuesta_raw", columnDefinition = "jsonb")
    private String respuestaRaw;

    @ManyToOne
    @JoinColumn(name = "usuario_consulta_id", nullable = false)
    private Usuario usuarioConsulta;

    @Column(name = "fecha_consulta", nullable = false)
    private LocalDateTime fechaConsulta;
}
