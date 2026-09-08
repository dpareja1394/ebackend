package co.edu.usbcali.autosusbcali.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "negociaciones")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Negociacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "oportunidad_id", nullable = false)
    private OportunidadVenta oportunidad;

    @ManyToOne
    @JoinColumn(name = "asesor_id", nullable = false)
    private Usuario asesor;

    @Column(name = "precio_ofertado", nullable = false, precision = 15, scale = 2)
    private BigDecimal precioOfertado;

    @Column(name = "descuento_porcentaje", precision = 5, scale = 2)
    private BigDecimal descuentoPorcentaje;

    @Column(name = "descuento_valor", precision = 15, scale = 2)
    private BigDecimal descuentoValor;

    @Column(name = "requiere_aprobacion", nullable = false)
    private Boolean requiereAprobacion;

    // Valores permitidos: PENDIENTE, APROBADA, RECHAZADA
    @Column(name = "estado_aprobacion", length = 20)
    private String estadoAprobacion;

    @ManyToOne
    @JoinColumn(name = "aprobador_id")
    private Usuario aprobador;

    @Column(name = "fecha_aprobacion")
    private OffsetDateTime fechaAprobacion;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;
}
