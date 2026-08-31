package co.edu.usbcali.dnetwork_java.domain.converter;

import co.edu.usbcali.dnetwork_java.domain.enums.TipoNotificacion;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoNotificacionConverter extends ValorDbEnumConverter<TipoNotificacion> {
    public TipoNotificacionConverter() {
        super(TipoNotificacion.class);
    }
}
