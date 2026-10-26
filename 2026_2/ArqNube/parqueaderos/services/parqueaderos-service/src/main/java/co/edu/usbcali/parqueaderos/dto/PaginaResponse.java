package co.edu.usbcali.parqueaderos.dto;

import java.util.List;
import org.springframework.data.domain.Page;

/** Respuesta paginada común a todos los listados (research.md R10). */
public record PaginaResponse<T>(List<T> contenido, int pagina, int tamano, long totalElementos, int totalPaginas) {

    public static <T> PaginaResponse<T> from(Page<T> page) {
        return new PaginaResponse<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }
}
