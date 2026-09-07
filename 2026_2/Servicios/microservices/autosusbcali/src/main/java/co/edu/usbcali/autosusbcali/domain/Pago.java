package co.edu.usbcali.autosusbcali.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    // Valores permitidos: CUOTA_INICIAL, SALDO, DESEMBOLSO_CREDITO
    @Column(name = "tipo_pago", length = 30, nullable = false)
    private String tipoPago;

    @Column(name = "monto", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    // Valores permitidos: TRANSFERENCIA, TARJETA, PSE, EFECTIVO, DESEMBOLSO
    @Column(name = "medio_pago", length = 30, nullable = false)
    private String medioPago;

    // Valores permitidos: PENDIENTE, PROCESANDO, APROBADO, RECHAZADO
    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    @Column(name = "referencia_externa", length = 100)
    private String referenciaExterna;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
}
