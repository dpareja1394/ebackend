package co.edu.usbcali.autosusbcali.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ventas")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_venta", length = 30, nullable = false, unique = true)
    private String numeroVenta;

    @OneToOne
    @JoinColumn(name = "oportunidad_id", nullable = false, unique = true)
    private OportunidadVenta oportunidad;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "comprador_id", nullable = false)
    private Usuario comprador;

    @ManyToOne
    @JoinColumn(name = "asesor_id", nullable = false)
    private Usuario asesor;

    @Column(name = "precio_final", nullable = false, precision = 15, scale = 2)
    private BigDecimal precioFinal;

    // Valores permitidos: CONTADO, CREDITO
    @Column(name = "forma_pago", length = 10, nullable = false)
    private String formaPago;

    @ManyToOne
    @JoinColumn(name = "solicitud_credito_id")
    private SolicitudCredito solicitudCredito;

    // Valores permitidos: EN_PROCESO, CERRADA, ANULADA
    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    @Column(name = "fecha_cierre")
    private OffsetDateTime fechaCierre;

    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;
}
