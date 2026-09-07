package co.edu.usbcali.autosusbcali.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "parametros_sistema")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ParametroSistema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "clave", length = 100, nullable = false, unique = true)
    private String clave;

    @Column(name = "valor", nullable = false)
    private String valor;

    // Valores permitidos: STRING, NUMBER, BOOLEAN, JSON
    @Column(name = "tipo_dato", length = 10, nullable = false)
    private String tipoDato;

    @Column(name = "descripcion")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "usuario_actualiza_id")
    private Usuario usuarioActualiza;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
