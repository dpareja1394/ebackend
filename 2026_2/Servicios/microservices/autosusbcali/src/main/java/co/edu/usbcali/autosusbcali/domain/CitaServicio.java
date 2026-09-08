package co.edu.usbcali.autosusbcali.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "citas_servicio")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CitaServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comprador_id", nullable = false)
    private Usuario comprador;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "asesor_id")
    private Usuario asesor;

    @Column(name = "tipo_servicio", length = 100, nullable = false)
    private String tipoServicio;

    @Column(name = "fecha_cita", nullable = false)
    private OffsetDateTime fechaCita;

    // Valores permitidos: AGENDADA, CONFIRMADA, REALIZADA, CANCELADA
    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;
}
