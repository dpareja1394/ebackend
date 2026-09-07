package co.edu.usbcali.autosusbcali.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fichas_tecnicas")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FichaTecnica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "vehiculo_id", nullable = false, unique = true)
    private Vehiculo vehiculo;

    @Column(name = "motor", length = 100)
    private String motor;

    @Column(name = "transmision", length = 50)
    private String transmision;

    @Column(name = "combustible", length = 30)
    private String combustible;

    @Column(name = "cilindraje_cc")
    private Integer cilindrajeCc;

    @Column(name = "potencia_hp")
    private Integer potenciaHp;

    @Column(name = "num_puertas")
    private Short numPuertas;

    @Column(name = "capacidad_pasajeros")
    private Short capacidadPasajeros;

    @Column(name = "num_airbags")
    private Short numAirbags;

    @Column(name = "url_documento_pdf", length = 500)
    private String urlDocumentoPdf;
}
