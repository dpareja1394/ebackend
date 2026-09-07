package co.edu.usbcali.autosusbcali.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehiculos")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Vehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vin", length = 17, nullable = false, unique = true)
    private String vin;

    @Column(name = "placa", length = 10, unique = true)
    private String placa;

    @ManyToOne
    @JoinColumn(name = "modelo_id", nullable = false)
    private Modelo modelo;

    @Column(name = "anio", nullable = false)
    private Short anio;

    // Valores permitidos: NUEVO, USADO
    @Column(name = "tipo", length = 6, nullable = false)
    private String tipo;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "kilometraje", nullable = false)
    private Integer kilometraje;

    @Column(name = "precio_base", nullable = false, precision = 15, scale = 2)
    private BigDecimal precioBase;

    // Valores permitidos: INGRESADO, EN_VERIFICACION, VERIFICADO, PUBLICADO, RESERVADO, EN_VENTA, VENDIDO, RETIRADO
    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "publicado_en")
    private LocalDateTime publicadoEn;

    @Column(name = "retirado_en")
    private LocalDateTime retiradoEn;

    @ManyToOne
    @JoinColumn(name = "usuario_registra_id", nullable = false)
    private Usuario usuarioRegistra;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
