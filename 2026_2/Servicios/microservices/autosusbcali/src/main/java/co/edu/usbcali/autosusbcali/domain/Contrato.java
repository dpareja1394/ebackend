package co.edu.usbcali.autosusbcali.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "contratos")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Contrato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "venta_id", nullable = false, unique = true)
    private Venta venta;

    @Column(name = "numero_contrato", length = 50, nullable = false, unique = true)
    private String numeroContrato;

    @Column(name = "url_documento", length = 500)
    private String urlDocumento;

    @Column(name = "firmado_comprador", nullable = false)
    private Boolean firmadoComprador;

    @Column(name = "firmado_asesor", nullable = false)
    private Boolean firmadoAsesor;

    @Column(name = "fecha_firma")
    private LocalDateTime fechaFirma;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion;
}
