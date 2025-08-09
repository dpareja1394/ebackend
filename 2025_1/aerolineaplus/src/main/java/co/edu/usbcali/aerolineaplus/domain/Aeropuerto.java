package co.edu.usbcali.aerolineaplus.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "aeropuertos")
public class Aeropuerto {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(length = 45)
    private String nombre;

    @Column(length = 45)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "idciudades", referencedColumnName = "id", nullable = false)
    private Ciudad ciudad;
}
