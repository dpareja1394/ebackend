package co.edu.usbcali.parqueaderos.dto;

import co.edu.usbcali.parqueaderos.exception.ValidacionException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/** Parámetros de paginación validados: pagina >= 0 y 1 <= tamano <= 100 (por defecto 0 y 20). */
public record Paginacion(int pagina, int tamano) {

    public static final int TAMANO_POR_DEFECTO = 20;
    public static final int TAMANO_MAXIMO = 100;

    public Paginacion {
        if (pagina < 0) {
            throw ValidacionException.campo("pagina", "Debe ser mayor o igual a 0");
        }
        if (tamano < 1 || tamano > TAMANO_MAXIMO) {
            throw ValidacionException.campo("tamano", "Debe estar entre 1 y " + TAMANO_MAXIMO);
        }
    }

    public static Paginacion de(Integer pagina, Integer tamano) {
        return new Paginacion(pagina == null ? 0 : pagina, tamano == null ? TAMANO_POR_DEFECTO : tamano);
    }

    public PageRequest toPageRequest(Sort sort) {
        return PageRequest.of(pagina, tamano, sort);
    }
}
