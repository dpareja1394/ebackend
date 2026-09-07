package co.edu.usbcali.autosusbcali.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "modelos", uniqueConstraints = @UniqueConstraint(columnNames = {"marca_id", "nombre"}))
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Modelo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "marca_id", nullable = false)
    private Marca marca;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;
}
