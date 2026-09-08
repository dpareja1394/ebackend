package co.edu.usbcali.autosusbcali.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "facturas")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "venta_id", nullable = false, unique = true)
    private Venta venta;

    @Column(name = "numero_factura", length = 50, nullable = false, unique = true)
    private String numeroFactura;

    @Column(name = "cufe", length = 255, unique = true)
    private String cufe;

    @Column(name = "url_documento", length = 500)
    private String urlDocumento;

    @Column(name = "subtotal", nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "iva", nullable = false, precision = 15, scale = 2)
    private BigDecimal iva;

    @Column(name = "total", nullable = false, precision = 15, scale = 2)
    private BigDecimal total;

    // Valores permitidos: GENERADA, ENVIADA_DIAN, ACEPTADA, RECHAZADA
    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    @Column(name = "fecha_emision", nullable = false)
    private OffsetDateTime fechaEmision;
}
