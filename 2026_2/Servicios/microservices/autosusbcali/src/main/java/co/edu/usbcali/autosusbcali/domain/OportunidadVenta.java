package co.edu.usbcali.autosusbcali.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "oportunidades_venta")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OportunidadVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "comprador_id", nullable = false)
    private Usuario comprador;

    @ManyToOne
    @JoinColumn(name = "asesor_id")
    private Usuario asesor;

    // Valores permitidos: NUEVA, EN_NEGOCIACION, COTIZACION_ENVIADA, CREDITO_EN_TRAMITE, GANADA, PERDIDA
    @Column(name = "estado", length = 30, nullable = false)
    private String estado;

    // Valores permitidos: WEB, PRESENCIAL, TELEFONO
    @Column(name = "origen", length = 20)
    private String origen;

    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private OffsetDateTime fechaActualizacion;
}
