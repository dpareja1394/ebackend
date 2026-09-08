package co.edu.usbcali.autosusbcali.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "pqrs")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Pqrs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_radicado", length = 30, nullable = false, unique = true)
    private String numeroRadicado;

    @ManyToOne
    @JoinColumn(name = "comprador_id", nullable = false)
    private Usuario comprador;

    // Valores permitidos: PETICION, QUEJA, RECLAMO, SUGERENCIA
    @Column(name = "tipo", length = 20, nullable = false)
    private String tipo;

    @Column(name = "asunto", length = 200, nullable = false)
    private String asunto;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "venta_id")
    private Venta venta;

    // Valores permitidos: RADICADA, EN_GESTION, RESUELTA, CERRADA
    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    @ManyToOne
    @JoinColumn(name = "usuario_asignado_id")
    private Usuario usuarioAsignado;

    @Column(name = "respuesta")
    private String respuesta;

    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;

    @Column(name = "fecha_cierre")
    private OffsetDateTime fechaCierre;
}
