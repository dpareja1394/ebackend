package co.edu.usbcali.autosusbcali.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tokens_recuperacion")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TokenRecuperacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "token", length = 255, nullable = false, unique = true)
    private String token;

    @Column(name = "usado", nullable = false)
    private Boolean usado;

    @Column(name = "fecha_expiracion", nullable = false)
    private OffsetDateTime fechaExpiracion;

    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;
}
