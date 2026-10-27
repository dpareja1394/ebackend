package co.edu.usbcali.autosusbcali.service;

import co.edu.usbcali.autosusbcali.dto.request.CrearModificarRolRequest;
import co.edu.usbcali.autosusbcali.dto.response.ObtenerRolResponse;

import java.util.List;

public interface IRolService {
    // Busca todos los roles sin importar su estado
    List<ObtenerRolResponse> obtenerTodos();

    // Buscamos los roles por estado (ya sea activo o inactivo)
    List<ObtenerRolResponse> buscarRolPorEstado(Boolean activo);

    ObtenerRolResponse obtenerRolPorNombre(String nombre);

    ObtenerRolResponse obtenerRolPorId(Long id);

    // Método para crear un rol
    ObtenerRolResponse crearRol(CrearModificarRolRequest crearModificarRolRequest) throws Exception;

    // Método para modificar un rol
    ObtenerRolResponse modificarRol(Long id, CrearModificarRolRequest modificarRolRequest) throws Exception;
}
