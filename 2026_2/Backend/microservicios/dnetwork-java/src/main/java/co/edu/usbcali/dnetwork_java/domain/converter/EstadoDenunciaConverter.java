package co.edu.usbcali.dnetwork_java.domain.converter;

import co.edu.usbcali.dnetwork_java.domain.enums.EstadoDenuncia;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EstadoDenunciaConverter extends ValorDbEnumConverter<EstadoDenuncia> {
    public EstadoDenunciaConverter() {
        super(EstadoDenuncia.class);
    }
}
