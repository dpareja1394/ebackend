package co.edu.usbcali.aerolineaplus.util.validation;

public class CiudadValidate {
    
    public static final String NOMBRE_NOT_NULL = "El Nombre de la Ciudad no puede ser nulo";
    public static final String NOMBRE_NOT_EMPTY = "El Nombre de la Ciudad no puede estar vacío";
    public static final String NOMBRE_NOT_VALID = "El Nombre de la Ciudad no cumple con el estándar";
    public static final String NOMBRE_REGEX = "^[A-Za-záéíóúÁÉÍÓÚñÑ0-9\\s\\-]+$";

    public static final String DESCRIPCION_NOT_NULL = "La Descripción de la Ciudad no puede ser nula";
    public static final String DESCRIPCION_NOT_EMPTY = "La Descripción de la Ciudad no puede estar vacía";
    public static final String DESCRIPCION_NOT_VALID = "La Descripción de la Ciudad no cumple con el estándar";
    public static final String DESCRIPCION_REGEX = "^[A-Za-záéíóúÁÉÍÓÚñÑ0-9\\s\\-]+$";

    public static final String PAIS_NOT_NULL = "El País relacionado con PaisId no puede ser nulo";
    public static final String PAIS_NOT_ZERO = "El PaisId no puede ser cero o menor que cero (0)";

}
