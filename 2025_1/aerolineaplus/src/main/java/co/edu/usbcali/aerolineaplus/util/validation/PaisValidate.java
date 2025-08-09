package co.edu.usbcali.aerolineaplus.util.validation;

public class PaisValidate {

    public static final String CODIGO_NOT_NULL = "El Código del País no puede ser nulo";
    public static final String CODIGO_NOT_EMPTY = "El Código del País no puede estar vacío";
    public static final String CODIGO_NOT_VALID = "El Código del País no cumple con el estándar";
    public static final String CODIGO_REGEX = "^[A-Za-záéíóúÁÉÍÓÚñÑ0-9\\s\\-]+$";

    public static final String NOMBRE_NOT_NULL = "El Nombre del País no puede ser nulo";
    public static final String NOMBRE_NOT_EMPTY = "El Nombre del País no puede estar vacío";
    public static final String NOMBRE_NOT_VALID = "El Nombre del País no cumple con el estándar";
    public static final String NOMBRE_REGEX = "^[A-Za-záéíóúÁÉÍÓÚñÑ0-9\\s\\-]+$";

}
