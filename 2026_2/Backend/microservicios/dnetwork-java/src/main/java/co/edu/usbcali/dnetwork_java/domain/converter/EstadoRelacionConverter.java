package co.edu.usbcali.dnetwork_java.domain.converter;

import co.edu.usbcali.dnetwork_java.domain.enums.EstadoRelacion;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EstadoRelacionConverter extends ValorDbEnumConverter<EstadoRelacion> {
    public EstadoRelacionConverter() {
        super(EstadoRelacion.class);
    }
}
