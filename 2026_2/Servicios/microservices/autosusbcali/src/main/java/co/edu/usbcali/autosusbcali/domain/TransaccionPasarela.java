package co.edu.usbcali.autosusbcali.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "transacciones_pasarela")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransaccionPasarela {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pago_id", nullable = false)
    private Pago pago;

    @Column(name = "proveedor", length = 50, nullable = false)
    private String proveedor;

    @Column(name = "referencia_pasarela", length = 100, nullable = false, unique = true)
    private String referenciaPasarela;

    @Column(name = "estado_pasarela", length = 50)
    private String estadoPasarela;

    @Column(name = "monto", precision = 15, scale = 2)
    private BigDecimal monto;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "respuesta_raw", columnDefinition = "jsonb")
    private String respuestaRaw;

    @Column(name = "fecha_transaccion", nullable = false)
    private OffsetDateTime fechaTransaccion;
}
