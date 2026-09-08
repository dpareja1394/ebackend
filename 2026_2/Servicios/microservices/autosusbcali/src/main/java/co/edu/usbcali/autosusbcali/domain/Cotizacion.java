package co.edu.usbcali.autosusbcali.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "cotizaciones")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Cotizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", length = 30, nullable = false, unique = true)
    private String codigo;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "comprador_id", nullable = false)
    private Usuario comprador;

    @ManyToOne
    @JoinColumn(name = "asesor_id")
    private Usuario asesor;

    @Column(name = "precio_base", nullable = false, precision = 15, scale = 2)
    private BigDecimal precioBase;

    @Column(name = "descuento", nullable = false, precision = 15, scale = 2)
    private BigDecimal descuento;

    @Column(name = "impuestos", nullable = false, precision = 15, scale = 2)
    private BigDecimal impuestos;

    @Column(name = "precio_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal precioTotal;

    @Column(name = "vigencia_hasta", nullable = false)
    private LocalDate vigenciaHasta;

    // Valores permitidos: BORRADOR, ENVIADA, ACEPTADA, RECHAZADA, VENCIDA
    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;
}
