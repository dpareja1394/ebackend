package co.edu.usbcali.dnetwork_java.service;

import co.edu.usbcali.dnetwork_java.dto.request.CrearPublicacionRequest;
import co.edu.usbcali.dnetwork_java.dto.response.ObtenerPublicacionResponse;

import java.util.List;

public interface PublicacionService {
    // Método para obtener TODAS las publicaciones
    List<ObtenerPublicacionResponse> obtenerPublicaciones();

    // Método para obtener una publicación por su Id
    ObtenerPublicacionResponse obtenerPublicacionPorId(Integer id) throws Exception;

    // Método para crear una nueva publicación en Base de Datos
    ObtenerPublicacionResponse crearPublicacion(CrearPublicacionRequest crearPublicacionRequest) throws Exception;
}
