package co.edu.usbcali.parqueaderos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;

@Entity
@Table(name = "espacios")
public class Espacio {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parqueadero_id", nullable = false, updatable = false)
    private Parqueadero parqueadero;

    /** Copia de solo lectura de la FK, para consultar sin cargar el parqueadero. */
    @Column(name = "parqueadero_id", insertable = false, updatable = false)
    private UUID parqueaderoId;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vehiculo", nullable = false, length = 20)
    private TipoVehiculo tipoVehiculo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEspacio estado = EstadoEspacio.DISPONIBLE;

    @Column(name = "vehiculo_id")
    private UUID vehiculoId;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;

    protected Espacio() {
    }

    public Espacio(Parqueadero parqueadero, String codigo, TipoVehiculo tipoVehiculo) {
        this.parqueadero = parqueadero;
        this.parqueaderoId = parqueadero.getId();
        setCodigo(codigo);
        this.tipoVehiculo = tipoVehiculo;
    }

    @PrePersist
    void alCrear() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        creadoEn = OffsetDateTime.now();
        actualizadoEn = creadoEn;
    }

    @PreUpdate
    void alActualizar() {
        actualizadoEn = OffsetDateTime.now();
    }

    /** El código se guarda en mayúsculas y sin espacios al inicio o al final. */
    public static String normalizarCodigo(String codigo) {
        return codigo == null ? null : codigo.trim().toUpperCase(Locale.ROOT);
    }

    public boolean estaOcupado() {
        return estado == EstadoEspacio.OCUPADO;
    }

    public UUID getId() {
        return id;
    }

    public Parqueadero getParqueadero() {
        return parqueadero;
    }

    public UUID getParqueaderoId() {
        return parqueaderoId;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = normalizarCodigo(codigo);
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(TipoVehiculo tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public EstadoEspacio getEstado() {
        return estado;
    }

    public void setEstado(EstadoEspacio estado) {
        this.estado = estado;
    }

    public UUID getVehiculoId() {
        return vehiculoId;
    }

    public OffsetDateTime getCreadoEn() {
        return creadoEn;
    }

    public OffsetDateTime getActualizadoEn() {
        return actualizadoEn;
    }
}
