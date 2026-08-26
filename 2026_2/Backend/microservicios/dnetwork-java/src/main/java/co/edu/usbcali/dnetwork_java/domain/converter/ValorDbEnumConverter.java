package co.edu.usbcali.dnetwork_java.domain.converter;

import co.edu.usbcali.dnetwork_java.domain.enums.IValorDbEnum;
import jakarta.persistence.AttributeConverter;

public abstract class ValorDbEnumConverter<E extends Enum<E> & IValorDbEnum> implements AttributeConverter<E, String> {

    private final Class<E> enumClass;

    protected ValorDbEnumConverter(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public String convertToDatabaseColumn(E attribute) {
        return attribute == null ? null : attribute.getValorDb();
    }

    @Override
    public E convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        for (E constant : enumClass.getEnumConstants()) {
            if (constant.getValorDb().equalsIgnoreCase(dbData)) {
                return constant;
            }
        }
        throw new IllegalArgumentException("Valor no reconocido para " + enumClass.getSimpleName() + ": " + dbData);
    }
}
